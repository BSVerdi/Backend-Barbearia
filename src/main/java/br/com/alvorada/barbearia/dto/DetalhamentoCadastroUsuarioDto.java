package br.com.alvorada.barbearia.dto;

import br.com.alvorada.barbearia.model.Usuario;

public record DetalhamentoCadastroUsuarioDto(
        Long id,
        String nome,
        String email,
        String tokenAcesso,
        String refreshToken
) {

  public DetalhamentoCadastroUsuarioDto(
          Usuario usuario,
          String tokenAcesso,
          String refreshToken
  ) {
    this(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            tokenAcesso,
            refreshToken
    );
  }
}
