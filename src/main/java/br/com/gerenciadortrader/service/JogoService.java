package br.com.gerenciadortrader.service;

import br.com.gerenciadortrader.dto.JogoResponseDTO;
import br.com.gerenciadortrader.model.Partida;
import br.com.gerenciadortrader.repository.PartidaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JogoService {

    private final PartidaRepository partidaRepository;

    /**
     * Retorna os jogos de uma data específica consultando o banco de dados local.
     * Os dados são previamente sincronizados pelo {@code SincronizacaoPartidasService},
     * isolando completamente o front-end da API externa.
     *
     * @param data data no formato {@code yyyy-MM-dd}
     * @return lista de {@link JogoResponseDTO} do dia
     */
    public List<JogoResponseDTO> buscarJogosPorData(String data) {
        return partidaRepository.findByDataStartingWith(data)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Converte uma entidade {@link Partida} para o DTO de saída {@link JogoResponseDTO},
     * preservando o contrato esperado pelo Controller e pelo front-end.
     *
     * @param partida entidade persistida no banco local
     * @return DTO correspondente
     */
    private JogoResponseDTO toDTO(Partida partida) {
        return new JogoResponseDTO(
                partida.getApiId(),
                partida.getData(),
                partida.getStatus(),
                partida.getLiga(),
                partida.getTimeCasa(),
                partida.getTimeVisitante(),
                partida.getEscudoCasa(),
                partida.getEscudoVisitante());
    }
}
