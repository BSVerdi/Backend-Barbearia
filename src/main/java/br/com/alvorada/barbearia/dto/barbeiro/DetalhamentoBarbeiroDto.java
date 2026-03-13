package br.com.alvorada.barbearia.dto.barbeiro;

import br.com.alvorada.barbearia.model.Barbeiro;

import java.time.LocalTime;

public record DetalhamentoBarbeiroDto(
        Long id,
        String nome,
        String email,
        String telefone,
        LocalTime horarioInicio,
        LocalTime horarioFim,
        Boolean ativo
) {

  public DetalhamentoBarbeiroDto(Barbeiro barbeiro) {
    this(
            barbeiro.getId(),
            barbeiro.getNome(),
            barbeiro.getEmail(),
            barbeiro.getTelefone(),
            barbeiro.getHorarioInicio(),
            barbeiro.getHorarioFim(),
            barbeiro.getAtivo()
    );
  }

}
