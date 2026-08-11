package br.com.gerenciadortrader.repository;

import br.com.gerenciadortrader.model.Estrategia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstrategiaRepository extends JpaRepository<Estrategia, Long> {
}
