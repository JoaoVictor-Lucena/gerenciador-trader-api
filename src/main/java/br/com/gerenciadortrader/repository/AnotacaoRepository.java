package br.com.gerenciadortrader.repository;

import br.com.gerenciadortrader.model.Anotacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnotacaoRepository extends JpaRepository<Anotacao, Long> {
}
