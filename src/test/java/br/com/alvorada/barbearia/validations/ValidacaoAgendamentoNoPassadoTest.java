package br.com.alvorada.barbearia.validations;

import br.com.alvorada.barbearia.dto.agendamento.AgendarDto;
import br.com.alvorada.barbearia.exception.RegraDeNegocioException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class ValidacaoAgendamentoNoPassadoTest {

  @InjectMocks
  private ValidacaoAgendamentoNoPassado validacaoAgendamento;

  @Test
  @DisplayName("Deveria permitir o agendamento no futuro")
  public void teste01() {
    var dto = new AgendarDto(
            LocalDateTime.now().plusDays(1).toString(),
            1L,
            1L
    );

    Assertions.assertDoesNotThrow(() -> validacaoAgendamento.validar(dto));
  }

  @Test
  @DisplayName("Deveria negar o agendamento no passado")
  public void teste02() {
    var dto = new AgendarDto(
            LocalDateTime.now().minusDays(1).toString(),
            1L,
            1L
    );

    Assertions.assertThrows(
            RegraDeNegocioException.class,
            () -> validacaoAgendamento.validar(dto)
    );
  }

}