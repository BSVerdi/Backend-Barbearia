package br.com.alvorada.barbearia.dto.cliente;

import br.com.alvorada.barbearia.model.Cliente;

public record DetalhamentoClienteDto(
        Long id,
        String nome,
        String email,
        String telefone,
        Boolean ativo
) {

  public DetalhamentoClienteDto(Cliente cliente) {
    this(
            cliente.getId(),
            cliente.getNome(),
            cliente.getEmail(),
            cliente.getTelefone(),
            cliente.getAtivo()
    );
  }

}
