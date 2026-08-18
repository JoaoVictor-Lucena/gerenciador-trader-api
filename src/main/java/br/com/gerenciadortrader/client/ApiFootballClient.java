package br.com.gerenciadortrader.client;

import br.com.gerenciadortrader.dto.JogoResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class ApiFootballClient {

    /**
     * Header de autenticação exigido pela football-data.org v4.
     * Diferente da api-sports (x-apisports-key), aqui é {@code X-Auth-Token}.
     */
    private static final String HEADER_AUTH = "X-Auth-Token";

    private final RestClient restClient;

    public ApiFootballClient(
            @Value("${api.football.url}") String baseUrl,
            @Value("${api.football.key}") String apiKey) {

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HEADER_AUTH, apiKey)
                .build();
    }

    /**
     * Busca as partidas de uma data específica na football-data.org v4.
     *
     * <p>Chamado exclusivamente pelo job de sincronização
     * {@code SincronizacaoPartidasService}; o banco de dados local é o cache
     * definitivo para as demais camadas.
     *
     * <p>O endpoint aceita {@code dateFrom} e {@code dateTo} no formato
     * {@code YYYY-MM-DD}. Passamos a mesma data nos dois parâmetros para obter
     * apenas os jogos do dia solicitado.
     *
     * @param data data no formato {@code YYYY-MM-DD}
     * @return lista de {@link JogoResponseDTO} com os dados essenciais de cada partida
     */
    public List<JogoResponseDTO> buscarJogosDoDia(String data) {
        ApiFootballWrapper wrapper = restClient.get()
                .uri("/matches?dateFrom={data}&dateTo={data}", data, data)
                .retrieve()
                .body(ApiFootballWrapper.class);

        if (wrapper == null || wrapper.matches() == null) {
            return List.of();
        }

        return wrapper.matches().stream()
                .map(match -> new JogoResponseDTO(
                        match.id(),
                        match.utcDate(),                   // ISO-8601 UTC — será convertido para BRT no SincronizacaoPartidasService
                        match.status(),                    // TIMED | IN_PLAY | FINISHED | CANCELLED | POSTPONED...
                        match.competition().name(),
                        match.homeTeam().name(),
                        match.awayTeam().name(),
                        match.homeTeam().crest(),          // campo "crest" na v4 (não "logo")
                        match.awayTeam().crest()))
                .toList();
    }
}

