package br.com.gerenciadortrader.repository;

import br.com.gerenciadortrader.model.Confronto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfrontoRepository extends JpaRepository<Confronto, Long> {
}
