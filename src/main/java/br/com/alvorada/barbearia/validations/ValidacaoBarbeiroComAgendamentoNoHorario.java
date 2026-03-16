package br.com.alvorada.barbearia.validations;

import br.com.alvorada.barbearia.dto.agendamento.AgendarDto;
import br.com.alvorada.barbearia.exception.RegraDeNegocioException;
import br.com.alvorada.barbearia.repository.AgendamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ValidacaoBarbeiroComAgendamentoNoHorario implements ValidacaoAgendamento {

  @Autowired
  private AgendamentoRepository repository;

  @Override
  public void validar(AgendarDto dto) {
    var existeAgendamentoNoHorario = repository.existsByBarbeiroIdAndDataHora(
            dto.barbeiroId(),
            LocalDateTime.parse(dto.dataHora())
    );

    if (existeAgendamentoNoHorario) {
      throw new RegraDeNegocioException("Já existe agendamento no horário desejado");
    }
  }

}
