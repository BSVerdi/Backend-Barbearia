package br.com.alvorada.barbearia.validations;

import br.com.alvorada.barbearia.dto.agendamento.AgendarDto;
import br.com.alvorada.barbearia.exception.RegraDeNegocioException;
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
class ValidacaoAgendamentoDentroDoHorairoDeTrabalhoTest {

  @InjectMocks
  private ValidacaoAgendamentoDentroDoHorairoDeTrabalho validacaoAgendamento;

  @Mock
  private BarbeiroRepository barbeiroRepository;

  @Test
  @DisplayName("Deveria permitir o agendamento com horário dentro do horário de trabalho")
  public void teste01() {
    var dto = new AgendarDto(
            LocalDateTime.now().withHour(14).toString(),
            1L,
            1L
    );

    var barbeiro = criarBarbeiroComTodosOsCampos();
    given(barbeiroRepository.findByIdAndAtivoTrue(any()))
            .willReturn(Optional.of(barbeiro));

    Assertions.assertDoesNotThrow(() -> validacaoAgendamento.validar(dto));
  }

  @Test
  @DisplayName("Deveria negar o agendamento com horário fora do horário de trabalho")
  public void teste02() {
    var dto = new AgendarDto(
            LocalDateTime.now().withHour(7).toString(),
            1L,
            1L
    );

    var barbeiro = criarBarbeiroComTodosOsCampos();
    given(barbeiroRepository.findByIdAndAtivoTrue(any()))
            .willReturn(Optional.of(barbeiro));

    Assertions.assertThrows(
            RegraDeNegocioException.class,
            () -> validacaoAgendamento.validar(dto)
    );
  }
}