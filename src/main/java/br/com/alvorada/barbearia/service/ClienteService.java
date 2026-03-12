package br.com.alvorada.barbearia.service;

import br.com.alvorada.barbearia.dto.cliente.*;
import br.com.alvorada.barbearia.exception.RegraDeNegocioException;
import br.com.alvorada.barbearia.model.Cliente;
import br.com.alvorada.barbearia.model.Perfil;
import br.com.alvorada.barbearia.model.Usuario;
import br.com.alvorada.barbearia.repository.ClienteRepository;
import br.com.alvorada.barbearia.security.service.TokenService;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

  private final ClienteRepository repository;
  private final UsuarioService usuarioService;
  private final TokenService tokenService;

  public ClienteService(ClienteRepository repository, UsuarioService usuarioService, TokenService tokenService) {
    this.repository = repository;
    this.usuarioService = usuarioService;
    this.tokenService = tokenService;
  }

  @Transactional
  public DetalhamentoCadastroClienteDto cadastrar(CadastroClienteDto dto) {
    var jaExiste = repository.existsByEmail(dto.email());

    if (jaExiste) {
      throw new RegraDeNegocioException("Usuário já existe");
    }

    var usuario = usuarioService.salvarUsuario(
            dto.nome(), dto.email(), dto.senha(), Perfil.CLIENTE
    );

    var cliente = repository.save(new Cliente(usuario.getId(), dto));
    var tokenAcesso = tokenService.gerarToken(usuario);
    var refreshToken = tokenService.gerarRefreshToken(usuario);

    return new DetalhamentoCadastroClienteDto(
            new DetalhamentoClienteDto(cliente), tokenAcesso, refreshToken
    );

  }

  @Transactional
  public List<ClienteDto> listarClientes() {
    return repository.findAll().stream()
            .map(ClienteDto::new)
            .toList();
  }

  @Transactional
  public void deletarCliente(Long id) {
    var cliente = repository.findById(id).orElseThrow();

    if (!cliente.getAtivo()) {
      throw new RegraDeNegocioException("Cliente inativo");
    }

    cliente.desativar();
    usuarioService.deletarUsuario(id);
  }

  @Transactional
  public DetalhamentoClienteDto atualizarDados(AtualizacaoDadosClienteDto dto) {
    var logado = (Usuario) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();

    if (logado == null) {
      throw new RegraDeNegocioException("Usuário não logado");
    }

    if (!logado.getId().equals(dto.id())) {
      throw new AccessDeniedException("Você não tem permissão para atualizar este usuario");
    }

    var cliente = repository.findById(dto.id()).orElseThrow();
    cliente.atualizarDados(dto);

    usuarioService.atualizarDados(dto.id(), dto.nome());

    return new DetalhamentoClienteDto(cliente);
  }

}
