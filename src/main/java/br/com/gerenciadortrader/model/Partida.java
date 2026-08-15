package br.com.gerenciadortrader.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade JPA que representa uma partida de futebol armazenada localmente.
 * Espelha os campos de {@link br.com.gerenciadortrader.dto.JogoResponseDTO},
 * eliminando a necessidade de consultar a API externa em tempo real.
 */
@Entity
@Table(name = "partidas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Partida {

    /** PK interna do banco de dados. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID único da partida na API-Football (fixture id).
     * Indexado para evitar duplicatas e acelerar buscas por fixture externo.
     */
    @Column(name = "api_id", nullable = false, unique = true)
    private Long apiId;

    /**
     * Data e hora da partida no formato ISO-8601
     * (ex.: 2026-08-12T15:00:00+00:00).
     * Armazenado como String para preservar o offset original da API.
     */
    @Column(nullable = false)
    private String data;

    /**
     * Status curto da partida conforme a API-Football
     * (ex.: NS, 1H, HT, 2H, FT, PST, CANC).
     */
    @Column(nullable = false, length = 10)
    private String status;

    /** Nome da liga/competição. */
    @Column(nullable = false)
    private String liga;

    /** Nome do time mandante (home). */
    @Column(nullable = false)
    private String timeCasa;

    /** Nome do time visitante (away). */
    @Column(nullable = false)
    private String timeVisitante;

    /** URL do escudo do time mandante. */
    @Column
    private String escudoCasa;

    /** URL do escudo do time visitante. */
    @Column
    private String escudoVisitante;
}
