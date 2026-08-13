package br.com.gerenciadortrader.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTOs internos para desserialização da resposta bruta da API-Football.
 * Mapeiam apenas os campos necessários; o restante é ignorado
 * via @JsonIgnoreProperties.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiFootballWrapper(List<FixtureItem> response) {

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
