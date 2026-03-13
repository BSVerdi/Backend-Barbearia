package br.com.alvorada.barbearia.validations;

import br.com.alvorada.barbearia.dto.agendamento.AgendarDto;

public interface ValidacaoAgendamento {
  void validar(AgendarDto dto);
}
