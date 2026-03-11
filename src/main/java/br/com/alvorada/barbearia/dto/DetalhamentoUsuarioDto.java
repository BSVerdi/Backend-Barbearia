package br.com.alvorada.barbearia.dto;

import br.com.alvorada.barbearia.model.Usuario;

public record DetalhamentoUsuarioDto(
        Long id,
        String nome,
        String email,
        Boolean ativo
) {
  public DetalhamentoUsuarioDto(Usuario usuario) {
    this(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getAtivo());
  }
}
