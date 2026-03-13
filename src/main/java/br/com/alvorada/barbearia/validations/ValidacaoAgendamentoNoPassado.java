package br.com.alvorada.barbearia.validations;

import br.com.alvorada.barbearia.dto.agendamento.AgendarDto;
import br.com.alvorada.barbearia.exception.RegraDeNegocioException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ValidacaoAgendamentoNoPassado implements ValidacaoAgendamento {

  @Override
  public void validar(AgendarDto dto) {
    var dataHora = LocalDateTime.parse(dto.dataHora());
    var hoje = LocalDateTime.now();

    if (dataHora.isBefore(hoje))
      throw new RegraDeNegocioException("Não é possível agendar no passado.");

  }

}
