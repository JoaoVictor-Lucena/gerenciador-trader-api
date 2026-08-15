package br.com.gerenciadortrader.service;

import br.com.gerenciadortrader.client.ApiFootballClient;
import br.com.gerenciadortrader.dto.JogoResponseDTO;
import br.com.gerenciadortrader.model.Partida;
import br.com.gerenciadortrader.repository.PartidaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Worker responsável por sincronizar antecipadamente os jogos da semana
 * com o banco de dados local, desacoplando o front-end da API externa.
 *
 * <p>
 * O job roda diariamente às 05h (horário de Brasília) e cobre
 * a janela de hoje + 3 dias seguintes, garantindo que o cache local
 * esteja sempre atualizado antes do início do tráfego do dia.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SincronizacaoPartidasService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final ZoneId ZONE_SP = ZoneId.of("America/Sao_Paulo");
    /** Hoje + quantos dias à frente o job deve cobrir. */
    private static final int JANELA_DIAS = 4;

    private final ApiFootballClient apiFootballClient;
    private final PartidaRepository partidaRepository;

    /**
     * Sincroniza os jogos dos próximos {@value #JANELA_DIAS} dias (hoje inclusive).
     *
     * <p>
     * Dispara todo dia às 05:00 (America/Sao_Paulo). A lógica é um upsert:
     * <ul>
     * <li>Jogo inexistente → persiste como nova {@link Partida}.</li>
     * <li>Jogo já existente → atualiza apenas o {@code status} (ex.: agendado →
     * cancelado).</li>
     * </ul>
     */

    @Scheduled(cron = "0 0 5 * * *", zone = "America/Sao_Paulo")
    @Transactional
    public void sincronizarJogosDaSemana() {
        log.info("[Sincronização] Iniciando job de sincronização de partidas...");

        int totalInseridos = 0;
        int totalAtualizados = 0;

        for (int offset = 0; offset < JANELA_DIAS; offset++) {
            LocalDate data = LocalDate.now().plusDays(offset);
            String dataFormatada = data.format(FORMATTER);

            log.info("[Sincronização] Buscando jogos para a data: {}", dataFormatada);

            List<JogoResponseDTO> jogos = apiFootballClient.buscarJogosDoDia(dataFormatada);
            log.info("[Sincronização] {} jogos encontrados para {}.", jogos.size(), dataFormatada);

            // Anti-bloqueio: aguarda 2 s entre chamadas para respeitar o rate limit da API
            try {
                Thread.sleep(2_000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                log.warn("[Sincronização] Thread interrompida durante o sleep de rate limit.", ie);
            }

            for (JogoResponseDTO jogo : jogos) {
                Optional<Partida> existente = partidaRepository.findByApiId(jogo.id());

                if (existente.isPresent()) {
                    // Upsert — atualiza somente o status para refletir mudanças (adiamento,
                    // cancelamento etc.)
                    Partida partida = existente.get();
                    partida.setStatus(jogo.status());
                    partidaRepository.save(partida);
                    totalAtualizados++;
                } else {
                    // Novo jogo — persiste todos os campos
                    // Converte o timestamp UTC da API para o fuso de Brasília antes de persistir,
                    // garantindo que findByDataStartingWith funcione para jogos noturnos.
                    String dataHoraBrasilia = ZonedDateTime
                            .parse(jogo.dataHora()) // parse do ISO-8601 com offset UTC
                            .withZoneSameInstant(ZONE_SP) // converte para America/Sao_Paulo
                            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME); // formata de volta p/ ISO-8601

                    Partida nova = Partida.builder()
                            .apiId(jogo.id())
                            .data(dataHoraBrasilia)
                            .status(jogo.status())
                            .liga(jogo.liga())
                            .timeCasa(jogo.timeCasa())
                            .timeVisitante(jogo.timeVisitante())
                            .escudoCasa(jogo.escudoCasa())
                            .escudoVisitante(jogo.escudoVisitante())
                            .build();
                    partidaRepository.save(nova);
                    totalInseridos++;
                }
            }
        }

        log.info("[Sincronização] Job finalizado. Inseridos: {}, Atualizados: {}.",
                totalInseridos, totalAtualizados);
    }
}
