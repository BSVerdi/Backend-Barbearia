package br.com.alvorada.barbearia.validations;

import br.com.alvorada.barbearia.dto.agendamento.AgendarDto;
import br.com.alvorada.barbearia.exception.RegraDeNegocioException;
import br.com.alvorada.barbearia.repository.AgendamentoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ValidacaoBarbeiroComAgendamentoNoHorarioTest {

  @InjectMocks
  private ValidacaoBarbeiroComAgendamentoNoHorario validacaoAgendamento;

  @Mock
  private AgendamentoRepository agendamentoRepository;

  @Test
  @DisplayName("Deveria permitir o agendamento com o barbeiro sem agendamento no mesmo horário")
  public void teste01() {
    var dto = new AgendarDto(LocalDateTime.now().toString(), 1L, 1L);

    given(agendamentoRepository.existsByBarbeiroIdAndDataHora(
            dto.barbeiroId(),
            LocalDateTime.parse(dto.dataHora())
    )).willReturn(false);

    Assertions.assertDoesNotThrow(() -> validacaoAgendamento.validar(dto));
  }

  @Test
  @DisplayName("Deveria negar o agendamento com o barbeiro com agendamento no mesmo horário")
  public void teste02() {
    var dto = new AgendarDto(LocalDateTime.now().toString(), 1L, 1L);

    given(agendamentoRepository.existsByBarbeiroIdAndDataHora(
            dto.barbeiroId(),
            LocalDateTime.parse(dto.dataHora())
    )).willReturn(true);

    Assertions.assertThrows(
            RegraDeNegocioException.class,
            () -> validacaoAgendamento.validar(dto)
    );
  }

}