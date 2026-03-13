package br.com.alvorada.barbearia.dto.agendamento;

import br.com.alvorada.barbearia.model.Agendamento;
import br.com.alvorada.barbearia.model.StatusAgendamento;

public record AgendamentoDto(
        Long id,
        String dataHora,
        StatusAgendamento status
) {

  public AgendamentoDto(Agendamento agendamento) {
    this(
            agendamento.getId(),
            agendamento.getDataHora().toString(),
            agendamento.getStatus()
    );
  }

}
