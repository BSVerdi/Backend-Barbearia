package br.com.alvorada.barbearia.dto.cliente;

import br.com.alvorada.barbearia.model.Cliente;

public record ClienteDto(
        Long id,
        String nome,
        String telefone,
        Integer agendamentos
) {

  public ClienteDto(Cliente cliente) {
    this(cliente.getId(), cliente.getNome(), cliente.getTelefone(), null);
  }

}
