package br.com.alvorada.barbearia.dto.agendamento;

import br.com.alvorada.barbearia.model.Agendamento;
import br.com.alvorada.barbearia.model.StatusAgendamento;

public record DetalhamentoAgendamentoDto(
        Long id,
        String dataHora,
        String cliente,
        String barbeiro,
        StatusAgendamento status
) {

  public DetalhamentoAgendamentoDto(Agendamento agendamento) {
    this(
            agendamento.getId(),
            agendamento.getDataHora().toString(),
            agendamento.getCliente().getNome(),
            agendamento.getBarbeiro().getNome(),
            agendamento.getStatus()
    );
  }

}
