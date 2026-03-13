package br.com.alvorada.barbearia.repository;

import br.com.alvorada.barbearia.model.Barbeiro;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BarbeiroRepository extends JpaRepository<Barbeiro, Long> {
  List<Barbeiro> findByAtivoTrue();

  boolean existsByEmailOrTelefone(String email, String telefone);

  Optional<Barbeiro> findByIdAndAtivoTrue(@NotNull Long aLong);
}
