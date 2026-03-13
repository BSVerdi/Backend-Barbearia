package br.com.alvorada.barbearia.repository;

import br.com.alvorada.barbearia.model.Cliente;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
  boolean existsByEmailOrTelefone(String email, String telefone);

  Optional<Cliente> findByIdAndAtivoTrue(@NotNull Long aLong);
}
