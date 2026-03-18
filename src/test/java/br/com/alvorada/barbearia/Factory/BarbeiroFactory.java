package br.com.alvorada.barbearia.Factory;

import br.com.alvorada.barbearia.dto.barbeiro.CadastroBarbeiroDto;
import br.com.alvorada.barbearia.model.Barbeiro;

public class BarbeiroFactory {

  public static Barbeiro criarBarbeiroComTodosOsCampos() {
    return new Barbeiro(
            1L,
            new CadastroBarbeiroDto(
                    "exemplo",
                    "exemplo@email.com",
                    "12345678910",
                    "09:00",
                    "18:00"
            )
    );
  }
}
