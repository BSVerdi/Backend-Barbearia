package br.com.alvorada.barbearia.service;

import br.com.alvorada.barbearia.dto.barbeiro.*;
import br.com.alvorada.barbearia.exception.RegraDeNegocioException;
import br.com.alvorada.barbearia.model.Agendamento;
import br.com.alvorada.barbearia.model.Barbeiro;
import br.com.alvorada.barbearia.model.Perfil;
import br.com.alvorada.barbearia.model.Usuario;
import br.com.alvorada.barbearia.repository.AgendamentoRepository;
import br.com.alvorada.barbearia.repository.BarbeiroRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BarbeiroService {

  private final BarbeiroRepository repository;
  private final UsuarioService usuarioService;
  private final AgendamentoRepository agendamentoRepository;

  public BarbeiroService(
          BarbeiroRepository repository,
          UsuarioService usuarioService,
          AgendamentoRepository agendamentoRepository
  ) {
    this.repository = repository;
    this.usuarioService = usuarioService;
    this.agendamentoRepository = agendamentoRepository;
  }

  @Transactional
  public List<BarbeiroDto> listarBarbeiros() {
    return repository.findAll().stream()
            .map(BarbeiroDto::new)
            .toList();
  }

  @Transactional
  public List<BarbeiroDto> listarBarbeirosAtivos() {
    return repository.findByAtivoTrue().stream()
            .map(BarbeiroDto::new)
            .toList();
  }

  @Transactional
  public DetalhamentoBarbeiroDto detalharBarbeiro(Long id) {
    var barbeiro = repository.findById(id).orElseThrow();

    return new DetalhamentoBarbeiroDto(barbeiro);
  }

  @Transactional
  public DetalhamentoBarbeiroDto cadastrar(CadastroBarbeiroDto dto) {
    var jaExiste = repository.existsByEmailOrTelefone(dto.email(), dto.telefone());

    if (jaExiste) {
      throw new RegraDeNegocioException("O email já está cadastrado");
    }

    var usuario = usuarioService.salvarUsuario(
            dto.nome(), dto.email(), dto.telefone(), Perfil.BARBEIRO
    );

    var barbeiro = repository.save(new Barbeiro(usuario.getId(), dto));

    return new DetalhamentoBarbeiroDto(barbeiro);
  }

  @Transactional
  public DetalhamentoBarbeiroDto atualizarDados(
          AtualizacaoDadosBarbeiroDto dto,
          Usuario logado
  ) {
    if (!(logado.ehDono(dto.id()) || logado.ehAdmin()))
      throw new AccessDeniedException("Usuario não tem permissão");

    var barbeiro = repository.findById(dto.id()).orElseThrow();
    barbeiro.atualizarDados(dto);

    usuarioService.atualizarDados(dto.id(), dto.nome());

    return new DetalhamentoBarbeiroDto(barbeiro);
  }

  @Transactional
  public void deletarBarbeiro(Long id, Usuario logado) {
    if (!(logado.ehDono(id) || logado.ehAdmin()))
      throw new AccessDeniedException("Usuario não tem permissão");

    var barbeiro = repository.findById(id).orElseThrow();
    barbeiro.desativar();

    usuarioService.deletarUsuario(id);
  }

  @Transactional
  public List<HorarioDto> listarHorariosDisponiveis(Long id, String data) {
    var barbeiro = repository.findById(id).orElseThrow();
    var horariosAgendados = listarHorariosAgendadosBarbeiroNaData(barbeiro, data);
    var horariosDisponiveis = new ArrayList<HorarioDto>();

    var horaAtual = barbeiro.getHorarioInicio();

    while (horaAtual.isBefore(barbeiro.getHorarioFim())) {
      var dataHora = LocalDateTime.parse(data + "T" + horaAtual);
      horaAtual = horaAtual.plusMinutes(30);

      if (!horariosAgendados.contains(dataHora))
        horariosDisponiveis.add(new HorarioDto(dataHora));
    }

    return horariosDisponiveis;
  }

  @Transactional
  private List<LocalDateTime> listarHorariosAgendadosBarbeiroNaData(
          Barbeiro barbeiro, String data
  ) {
    var inicio = LocalDateTime.parse(data + "T" + barbeiro.getHorarioInicio());
    var fim = LocalDateTime.parse(data + "T" + barbeiro.getHorarioFim());

    var agendamentos = agendamentoRepository.findByBarbeiroIdAndDataHoraBetween(
            barbeiro.getId(), inicio, fim
    );

    return agendamentos.stream()
            .map(Agendamento::getDataHora)
            .collect(Collectors.toList());
  }

  public DetalhamentoBarbeiroDto reativarBarbeiro(Long id) {
    var barbeiro = repository.findById(id).orElseThrow();

    return new DetalhamentoBarbeiroDto(barbeiro);
  }
}
