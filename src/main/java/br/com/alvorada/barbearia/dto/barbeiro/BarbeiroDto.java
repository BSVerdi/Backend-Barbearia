package br.com.alvorada.barbearia.dto.barbeiro;

import br.com.alvorada.barbearia.model.Barbeiro;

public record BarbeiroDto(
        Long id,
        String nome,
        Boolean ativo
) {

  public BarbeiroDto(Barbeiro barbeiro) {
    this(barbeiro.getId(), barbeiro.getNome(), barbeiro.getAtivo());
  }
}
