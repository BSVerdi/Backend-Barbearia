package br.com.alvorada.barbearia.dto.usuario;

import br.com.alvorada.barbearia.model.Perfil;
import br.com.alvorada.barbearia.model.Usuario;

public record DetalhamentoUsuarioDto(
        Long id,
        String nome,
        String email,
        Perfil perfil,
        Boolean ativo
) {
  public DetalhamentoUsuarioDto(Usuario usuario) {
    this(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getPerfil(),
            usuario.getAtivo()
    );
  }
}
