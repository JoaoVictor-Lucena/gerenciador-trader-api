package br.com.gerenciadortrader.client;

import br.com.gerenciadortrader.dto.JogoResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class ApiFootballClient {

        private static final String HEADER_API_KEY = "x-apisports-key";

        private final RestClient restClient;

        public ApiFootballClient(
                        @Value("${api.football.url}") String baseUrl,
                        @Value("${api.football.key}") String apiKey) {

                this.restClient = RestClient.builder()
                                .baseUrl(baseUrl)
                                .defaultHeader(HEADER_API_KEY, apiKey)
                                .build();
        }

        /**
         * Busca os jogos (fixtures) de uma data específica na API-Football.
         * Chamado exclusivamente pelo job de sincronização {@code SincronizacaoPartidasService};
         * o banco de dados local é o cache definitivo para as demais camadas.
         *
         * @param data data no formato 'YYYY-MM-DD'
         * @return lista de {@link JogoResponseDTO} com os dados essenciais de cada
         *         partida
         */
        public List<JogoResponseDTO> buscarJogosDoDia(String data) {
                ApiFootballWrapper wrapper = restClient.get()
                                .uri("/fixtures?date={data}&timezone=America/Sao_Paulo", data)
                                .retrieve()
                                .body(ApiFootballWrapper.class);

                if (wrapper == null || wrapper.response() == null) {
                        return List.of();
                }

                return wrapper.response().stream()
                                .map(item -> new JogoResponseDTO(
                                                item.fixture().id(),
                                                item.fixture().date(),
                                                item.fixture().status().statusCurto(),
                                                item.league().name(),
                                                item.teams().home().name(),
                                                item.teams().away().name(),
                                                item.teams().home().logo(),
                                                item.teams().away().logo()))
                                .toList();
        }
}
