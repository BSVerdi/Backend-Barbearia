package br.com.alvorada.barbearia.service;

import br.com.alvorada.barbearia.dto.barbeiro.AtualizacaoDadosBarbeiroDto;
import br.com.alvorada.barbearia.dto.barbeiro.BarbeiroDto;
import br.com.alvorada.barbearia.dto.barbeiro.CadastroBarbeiroDto;
import br.com.alvorada.barbearia.dto.barbeiro.DetalhamentoBarbeiroDto;
import br.com.alvorada.barbearia.exception.RegraDeNegocioException;
import br.com.alvorada.barbearia.model.Barbeiro;
import br.com.alvorada.barbearia.model.Perfil;
import br.com.alvorada.barbearia.model.Usuario;
import br.com.alvorada.barbearia.repository.BarbeiroRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BarbeiroService {

  private final BarbeiroRepository repository;
  private final UsuarioService usuarioService;

  public BarbeiroService(BarbeiroRepository repository, UsuarioService usuarioService, PasswordEncoder encoder) {
    this.repository = repository;
    this.usuarioService = usuarioService;
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
    var jaExiste = repository.existsByEmail(dto.email());

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

}
