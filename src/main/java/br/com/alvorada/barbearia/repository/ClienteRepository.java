package br.com.alvorada.barbearia.repository;

import br.com.alvorada.barbearia.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
  boolean existsByEmail(String email);
}
