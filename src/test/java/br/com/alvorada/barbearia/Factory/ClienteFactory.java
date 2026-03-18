package br.com.alvorada.barbearia.Factory;

import br.com.alvorada.barbearia.model.Cliente;

import java.util.ArrayList;

public class ClienteFactory {

  public static Cliente criarClienteComTodosOsCampos() {
    return new Cliente(
            1L,
            "exemplo",
            "exemplo@email.com",
            "12345678910",
            true,
            new ArrayList<>()
    );
  }
}
