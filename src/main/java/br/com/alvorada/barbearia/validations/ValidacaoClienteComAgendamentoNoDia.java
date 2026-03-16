package br.com.alvorada.barbearia.validations;

import br.com.alvorada.barbearia.dto.agendamento.AgendarDto;
import br.com.alvorada.barbearia.exception.RegraDeNegocioException;
import br.com.alvorada.barbearia.repository.AgendamentoRepository;
import br.com.alvorada.barbearia.repository.BarbeiroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ValidacaoClienteComAgendamentoNoDia implements ValidacaoAgendamento {

  @Autowired
  private AgendamentoRepository agendamentoRepository;

  @Autowired
  private BarbeiroRepository barbeiroRepository;

  @Override
  public void validar(AgendarDto dto) {
    var barbeiro = barbeiroRepository.findByIdAndAtivoTrue(dto.barbeiroId()).orElseThrow();

    var data = (LocalDateTime.parse(dto.dataHora()).toLocalDate()).toString();
    var inicio = LocalDateTime.parse(data + "T" + barbeiro.getHorarioInicio());
    var fim = LocalDateTime.parse(data + "T" + barbeiro.getHorarioFim());

    var jaExisteAgendamentoNaData = agendamentoRepository
            .existsByClienteIdAndDataHoraBetween(dto.clienteId(), inicio, fim);

    if (jaExisteAgendamentoNaData)
      throw new RegraDeNegocioException("O cliente já possui outro agendamento nessa data");
  }
}
