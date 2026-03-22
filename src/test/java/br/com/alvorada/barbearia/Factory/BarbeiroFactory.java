package br.com.alvorada.barbearia.Factory;

import br.com.alvorada.barbearia.dto.barbeiro.AtualizacaoDadosBarbeiroDto;
import br.com.alvorada.barbearia.dto.barbeiro.CadastroBarbeiroDto;
import br.com.alvorada.barbearia.model.Barbeiro;

import java.time.LocalTime;

public class BarbeiroFactory {

  public static Barbeiro criarBarbeiroComTodosOsCampos() {
    return new Barbeiro(
            1L,
            criarCadastroBarbeiroComTodosOsCampos()
    );
  }

  public static CadastroBarbeiroDto criarCadastroBarbeiroComTodosOsCampos() {
    return new CadastroBarbeiroDto(
            "exemplo",
            "exemplo@email.com",
            "12345678910",
            "09:00",
            "18:00"
    );
  }

  public static AtualizacaoDadosBarbeiroDto criarAtualizacaoBarbeiroComTodosOsCampos() {
    return new AtualizacaoDadosBarbeiroDto(
            1L,
            "novoNomeExemplo",
            "10987654321",
            LocalTime.parse("12:00"),
            LocalTime.parse("13:00")
    );
  }

}
