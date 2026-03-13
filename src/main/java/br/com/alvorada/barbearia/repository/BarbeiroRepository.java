package br.com.alvorada.barbearia.repository;

import br.com.alvorada.barbearia.model.Barbeiro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BarbeiroRepository extends JpaRepository<Barbeiro, Long> {
  List<Barbeiro> findByAtivoTrue();

  boolean existsByEmail(String email);
}
