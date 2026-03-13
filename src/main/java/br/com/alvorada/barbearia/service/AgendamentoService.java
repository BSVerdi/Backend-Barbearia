package br.com.alvorada.barbearia.service;

import br.com.alvorada.barbearia.dto.agendamento.AgendamentoDto;
import br.com.alvorada.barbearia.dto.agendamento.AgendarDto;
import br.com.alvorada.barbearia.dto.agendamento.DetalhamentoAgendamentoDto;
import br.com.alvorada.barbearia.model.Agendamento;
import br.com.alvorada.barbearia.model.Usuario;
import br.com.alvorada.barbearia.repository.AgendamentoRepository;
import br.com.alvorada.barbearia.repository.BarbeiroRepository;
import br.com.alvorada.barbearia.repository.ClienteRepository;
import br.com.alvorada.barbearia.validations.ValidacaoAgendamento;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgendamentoService {

  private final AgendamentoRepository repository;
  private final BarbeiroRepository barbeiroRepository;
  private final ClienteRepository clienteRepository;
  private final List<ValidacaoAgendamento> validacoes;

  public AgendamentoService(
          AgendamentoRepository repository,
          BarbeiroRepository barbeiroRepository,
          ClienteRepository clienteRepository,
          List<ValidacaoAgendamento> validacoes
  ) {
    this.repository = repository;
    this.barbeiroRepository = barbeiroRepository;
    this.clienteRepository = clienteRepository;
    this.validacoes = validacoes;
  }

  @Transactional
  public DetalhamentoAgendamentoDto agendar(AgendarDto dto, Usuario logado) {
    if (!logado.ehDono(dto.clienteId()))
      throw new AccessDeniedException("Você não pode marcar por outras pessoas.");

    validacoes.forEach(v -> v.validar(dto));

    var cliente = clienteRepository.findByIdAndAtivoTrue(dto.clienteId()).orElseThrow();
    var barbeiro = barbeiroRepository.findByIdAndAtivoTrue(dto.barbeiroId()).orElseThrow();

    var agendamento = new Agendamento(dto.dataHora(), cliente, barbeiro);

    repository.save(agendamento);

    return new DetalhamentoAgendamentoDto(agendamento);
  }

  @Transactional
  public List<AgendamentoDto> listarAgendamentosCliente(Long id, Usuario logado) {
    if (!(logado.ehDono(id) || logado.ehAdmin()))
      throw new AccessDeniedException("Usuário sem permissão.");

    var agendamentos = repository.findByClienteId(id);

    return agendamentos.stream()
            .map(AgendamentoDto::new)
            .toList();
  }

  @Transactional
  public List<AgendamentoDto> listarAgendamentosBarbeiro(Long id, Usuario logado) {
    if (!(logado.ehDono(id) || logado.ehAdmin()))
      throw new AccessDeniedException("Usuário sem permissão.");

    var agendamentos = repository.findByBarbeiroId(id);

    return agendamentos.stream()
            .map(AgendamentoDto::new)
            .toList();
  }

  @Transactional
  public List<AgendamentoDto> listarAgendamentos() {
    var agendamentos = repository.findAll();

    return agendamentos.stream()
            .map(AgendamentoDto::new)
            .toList();
  }

  @Transactional
  public DetalhamentoAgendamentoDto detalharAgendamento(Long id, Usuario logado) {
    var agendamento = repository.findById(id).orElseThrow();

    var barbeiroId = agendamento.getBarbeiro().getId();
    var clienteId = agendamento.getCliente().getId();

    if (!(logado.ehDono(barbeiroId) || logado.ehDono(clienteId) || logado.ehAdmin()))
      throw new AccessDeniedException("Usuário sem permissão.");

    return new DetalhamentoAgendamentoDto(agendamento);
  }

}
