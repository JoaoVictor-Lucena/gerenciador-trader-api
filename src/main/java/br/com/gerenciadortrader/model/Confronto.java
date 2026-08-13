package br.com.gerenciadortrader.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Confronto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime horario;

    private String times;

    private String campeonato;

    private String escudoCasa;

    private String escudoVisitante;

    private BigDecimal odds;

    @Enumerated(EnumType.STRING)
    private StatusOperacao status;

    @OneToMany(mappedBy = "confronto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Anotacao> anotacoes;

    @ManyToMany
    private List<Estrategia> estrategias;
}
