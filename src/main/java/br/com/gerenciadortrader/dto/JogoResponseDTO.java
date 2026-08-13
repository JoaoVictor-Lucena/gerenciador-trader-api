package br.com.gerenciadortrader.dto;

/**
 * DTO de saída limpo exposto para as outras camadas da aplicação.
 * Representa um jogo (fixture) retornado pela API-Football.
 *
 * @param id            ID único da partida na API-Football
 * @param dataHora      Data e hora da partida no formato ISO-8601 (ex:
 *                      2026-08-12T15:00:00+00:00)
 * @param status        Status curto da partida (ex: NS, 1H, HT, 2H, FT, PST,
 *                      CANC)
 * @param liga          Nome da liga/competição
 * @param timeCasa      Nome do time mandante (home)
 * @param timeVisitante Nome do time visitante (away)
 * @param escudoCasa    URL do escudo do time mandante (home logo)
 * @param escudoVisitante URL do escudo do time visitante (away logo)
 */
public record JogoResponseDTO(
                Long id,
                String dataHora,
                String status,
                String liga,
                String timeCasa,
                String timeVisitante,
                String escudoCasa,
                String escudoVisitante) {
}
