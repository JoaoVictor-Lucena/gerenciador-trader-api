package br.com.gerenciadortrader.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTOs internos para desserialização da resposta bruta da API-Football
 * (api-sports v3).
 *
 * <p>
 * Estrutura do endpoint
 * {@code GET /fixtures?date={data}&timezone=America/Sao_Paulo}:
 * 
 * <pre>
 * {
 *   "response": [
 *     {
 *       "fixture": {
 *         "id": 123456,
 *         "date": "2026-08-18T20:00:00-03:00",   ← já em BRT por causa do timezone param
 *         "status": { "short": "NS" }
 *       },
 *       "league": { "name": "Brasileirão Série A" },
 *       "teams": {
 *         "home": { "name": "Flamengo",  "logo": "https://media.api-sports.io/football/teams/44.png" },
 *         "away": { "name": "Palmeiras", "logo": "https://media.api-sports.io/football/teams/45.png" }
 *       }
 *     }
 *   ]
 * }
 * </pre>
 *
 * <p>
 * Campos não mapeados são ignorados via {@code @JsonIgnoreProperties}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiFootballWrapper(List<FixtureItem> response) {

        /** Representa um elemento do array {@code response}. */
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record FixtureItem(
                        FixtureDetails fixture,
                        LeagueInfo league,
                        TeamsInfo teams) {
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record FixtureDetails(
                        @JsonProperty("id") Long id,
                        @JsonProperty("date") String date,
                        @JsonProperty("status") StatusInfo status) {
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record StatusInfo(
                        // "short" é palavra reservada em Java — mapeamos com @JsonProperty
                        @JsonProperty("short") String statusCurto) {
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record LeagueInfo(String name) {
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record TeamsInfo(
                        @JsonProperty("home") TeamInfo home,
                        @JsonProperty("away") TeamInfo away) {
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record TeamInfo(
                        @JsonProperty("name") String name,
                        @JsonProperty("logo") String logo) {
        }
}
