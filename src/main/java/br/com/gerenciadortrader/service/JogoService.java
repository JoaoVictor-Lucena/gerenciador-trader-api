package br.com.gerenciadortrader.service;

import br.com.gerenciadortrader.client.ApiFootballClient;
import br.com.gerenciadortrader.dto.JogoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JogoService {

    private final ApiFootballClient apiFootballClient;

    /**
     * Retorna os jogos de uma data específica consultando a API-Football.
     *
     * @param data data no formato 'YYYY-MM-DD'
     * @return lista de jogos do dia
     */
    public List<JogoResponseDTO> buscarJogosPorData(String data) {
        return apiFootballClient.buscarJogosDoDia(data);
    }
}
