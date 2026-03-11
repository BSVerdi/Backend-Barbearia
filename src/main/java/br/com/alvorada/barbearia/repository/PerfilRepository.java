package br.com.alvorada.barbearia.repository;

import br.com.alvorada.barbearia.model.Perfil;
import br.com.alvorada.barbearia.model.PerfilNome;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerfilRepository extends JpaRepository<Perfil, Long> {
  Perfil findByNome(PerfilNome perfilNome);
}
