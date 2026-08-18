package br.com.gerenciadortrader.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * DTOs internos para desserialização da resposta bruta da football-data.org v4.
 *
 * <p>Estrutura do endpoint {@code GET /v4/matches?dateFrom={data}&dateTo={data}}:
 * <pre>
 * {
 *   "matches": [
 *     {
 *       "id": 123456,
 *       "utcDate": "2026-08-18T19:00:00Z",
 *       "status": "TIMED",
 *       "competition": { "name": "Brasileirão Série A" },
 *       "homeTeam":    { "name": "Flamengo", "crest": "https://crests.football-data.org/71.svg" },
 *       "awayTeam":    { "name": "Palmeiras", "crest": "https://crests.football-data.org/72.svg" }
 *     }
 *   ]
 * }
 * </pre>
 *
 * <p>Status possíveis na v4: {@code TIMED, SCHEDULED, IN_PLAY, PAUSED, FINISHED,
 * CANCELLED, SUSPENDED, POSTPONED}. Campos não mapeados são ignorados via
 * {@code @JsonIgnoreProperties}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiFootballWrapper(List<MatchItem> matches) {

    /** Representa um elemento do array {@code matches}. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MatchItem(
            Long id,
            String utcDate,
            String status,
            CompetitionInfo competition,
            TeamInfo homeTeam,
            TeamInfo awayTeam) {
    }

    /** Subrecord para o campo {@code competition}. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CompetitionInfo(String name) {
    }

    /**
     * Subrecord reutilizado tanto para {@code homeTeam} quanto para {@code awayTeam}.
     * O campo do escudo na v4 é {@code crest} (e não {@code logo} como na api-sports).
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TeamInfo(String name, String crest) {
    }
}

