package br.com.gerenciadortrader.repository;

import br.com.gerenciadortrader.model.Partida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório JPA para a entidade {@link Partida}.
 * Fornece operações CRUD padrão via {@link JpaRepository} e consultas
 * derivadas para o acesso local às partidas sincronizadas.
 */
@Repository
public interface PartidaRepository extends JpaRepository<Partida, Long> {

    /**
     * Retorna todas as partidas cuja {@code data} (String ISO-8601) começa
     * com a data informada, permitindo buscar por dia independentemente do
     * horário e do offset armazenado.
     *
     * <p>Exemplo de uso:
     * <pre>{@code
     *   // Retorna todas as partidas do dia 2026-08-12
     *   repository.findByDataStartingWith("2026-08-12");
     * }</pre>
     *
     * @param data prefixo da data no formato {@code yyyy-MM-dd}
     * @return lista de partidas que correspondem à data informada
     */
    List<Partida> findByDataStartingWith(String data);

    /**
     * Busca uma partida pelo ID da fixture na API-Football.
     * Usado pelo job de sincronização para implementar a lógica de upsert:
     * se o jogo já existe, apenas o status é atualizado; caso contrário,
     * uma nova entrada é persistida.
     *
     * @param apiId ID do fixture retornado pela API-Football
     * @return {@link Optional} contendo a partida, ou vazio se não encontrada
     */
    Optional<Partida> findByApiId(Long apiId);
}
