package br.com.alvorada.barbearia.validations;

import br.com.alvorada.barbearia.dto.agendamento.AgendarDto;
import br.com.alvorada.barbearia.exception.RegraDeNegocioException;
import br.com.alvorada.barbearia.repository.BarbeiroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ValidacaoAgendamentoDentroDoHorairoDeTrabalho implements ValidacaoAgendamento {

  @Autowired
  private BarbeiroRepository repository;

  @Override
  public void validar(AgendarDto dto) {
    var barbeiro = repository.findByIdAndAtivoTrue(dto.barbeiroId()).orElseThrow();
    var dataHora = LocalDateTime.parse(dto.dataHora()).toLocalTime();

    if (dataHora.isBefore(barbeiro.getHorarioInicio()) || dataHora.isAfter(barbeiro.getHorarioFim())) {
      throw new RegraDeNegocioException("Agendamento fora do horário de trabalho");
    }
  }

}
