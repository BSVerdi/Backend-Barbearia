package br.com.alvorada.barbearia.validations;

import br.com.alvorada.barbearia.dto.agendamento.AgendarDto;
import br.com.alvorada.barbearia.exception.RegraDeNegocioException;
import br.com.alvorada.barbearia.repository.AgendamentoRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ValidacaoBarbeiroComAgendamentoNoHorario implements ValidacaoAgendamento {

  private final AgendamentoRepository repository;

  public ValidacaoBarbeiroComAgendamentoNoHorario(AgendamentoRepository repository) {
    this.repository = repository;
  }

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
