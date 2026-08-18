package br.com.gerenciadortrader.client;

import br.com.gerenciadortrader.dto.JogoResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class ApiFootballClient {

        /**
         * Header de autenticação da API-Football (api-sports v3).
         * A football-data.org usava {@code X-Auth-Token}; aqui voltamos ao original.
         */
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
         * Busca os jogos (fixtures) de uma data específica na API-Football (api-sports
         * v3).
         *
         * <p>
         * Chamado exclusivamente pelo job de sincronização
         * {@code SincronizacaoPartidasService}; o banco de dados local é o cache
         * definitivo para as demais camadas.
         *
         * <p>
         * O parâmetro {@code timezone=America/Sao_Paulo} faz a api-sports retornar
         * o campo {@code fixture.date} já no fuso de Brasília (ex.:
         * {@code 2026-08-18T20:00:00-03:00}), eliminando a necessidade de conversão
         * adicional. A transformação {@code ZonedDateTime} no Service permanece como
         * salvaguarda idempotente para eventuais inconsistências.
         *
         * @param data data no formato {@code YYYY-MM-DD}
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
                                                item.fixture().date(), // já em BRT graças ao timezone param
                                                item.fixture().status().statusCurto(), // NS, 1H, HT, 2H, FT, PST,
                                                                                       // CANC...
                                                item.league().name(),
                                                item.teams().home().name(),
                                                item.teams().away().name(),
                                                item.teams().home().logo(),
                                                item.teams().away().logo()))
                                .toList();
        }
}
