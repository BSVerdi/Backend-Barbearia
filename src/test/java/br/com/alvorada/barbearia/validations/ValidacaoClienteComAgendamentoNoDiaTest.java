package br.com.alvorada.barbearia.validations;

import br.com.alvorada.barbearia.dto.agendamento.AgendarDto;
import br.com.alvorada.barbearia.exception.RegraDeNegocioException;
import br.com.alvorada.barbearia.repository.AgendamentoRepository;
import br.com.alvorada.barbearia.repository.BarbeiroRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static br.com.alvorada.barbearia.Factory.BarbeiroFactory.criarBarbeiroComTodosOsCampos;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ValidacaoClienteComAgendamentoNoDiaTest {

  @InjectMocks
  private ValidacaoClienteComAgendamentoNoDia validacaoAgendamento;

  @Mock
  private AgendamentoRepository agendamentoRepository;

  @Mock
  private BarbeiroRepository barbeiroRepository;

  @Test
  @DisplayName("Deveria permitir o agendamento do cliente sem outro agendamentos no dia")
  public void deveriaPermitirOAgendamento() {
    var barbeiro = criarBarbeiroComTodosOsCampos();
    given(barbeiroRepository.findByIdAndAtivoTrue(any()))
            .willReturn(Optional.of(barbeiro));

    var dto = new AgendarDto(LocalDateTime.now().toString(), 1L, 1L);
    given(agendamentoRepository.existsByClienteIdAndDataHoraBetween(
            any(), any(), any()
    )).willReturn(false);

    Assertions.assertDoesNotThrow(() -> validacaoAgendamento.validar(dto));
  }

  @Test
  @DisplayName("Deveria negar o agendamento do cliente com outro agendamentos no dia")
  public void deveriaNegarOAgendamento() {
    var barbeiro = criarBarbeiroComTodosOsCampos();
    given(barbeiroRepository.findByIdAndAtivoTrue(any()))
            .willReturn(Optional.of(barbeiro));

    var dto = new AgendarDto(LocalDateTime.now().toString(), 1L, 1L);
    given(agendamentoRepository.existsByClienteIdAndDataHoraBetween(
            any(), any(), any()
    )).willReturn(true);

    Assertions.assertThrows(
            RegraDeNegocioException.class,
            () -> validacaoAgendamento.validar(dto)
    );
  }

}