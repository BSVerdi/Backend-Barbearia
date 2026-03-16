package br.com.alvorada.barbearia.service;

import br.com.alvorada.barbearia.dto.cliente.*;
import br.com.alvorada.barbearia.exception.RegraDeNegocioException;
import br.com.alvorada.barbearia.model.Cliente;
import br.com.alvorada.barbearia.model.Perfil;
import br.com.alvorada.barbearia.model.Usuario;
import br.com.alvorada.barbearia.repository.ClienteRepository;
import br.com.alvorada.barbearia.security.service.TokenService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

  @Autowired
  private ClienteRepository repository;

  @Autowired
  private UsuarioService usuarioService;

  @Autowired
  private TokenService tokenService;

  @Transactional
  public DetalhamentoCadastroClienteDto cadastrar(CadastroClienteDto dto) {
    var jaExiste = repository.existsByEmailOrTelefone(dto.email(), dto.telefone());

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
  public void deletarCliente(Long id, Usuario logado) {
    if (!(logado.ehDono(id) || logado.ehAdmin()))
      throw new AccessDeniedException("Usuário não tem permissão");

    var cliente = repository.findById(id).orElseThrow();

    if (!cliente.getAtivo()) {
      throw new RegraDeNegocioException("O cliente já está inativo.");
    }

    cliente.desativar();
    usuarioService.deletarUsuario(id);
  }

  @Transactional
  public DetalhamentoClienteDto atualizarDados(
          AtualizacaoDadosClienteDto dto, Usuario logado
  ) {
    if (!logado.ehDono(dto.id())) {
      throw new AccessDeniedException("Você não tem permissão para atualizar este usuario");
    }

    var cliente = repository.findById(dto.id()).orElseThrow();
    cliente.atualizarDados(dto);

    usuarioService.atualizarDados(dto.id(), dto.nome());

    return new DetalhamentoClienteDto(cliente);
  }

  @Transactional
  public DetalhamentoClienteDto detalharCliente(Long id, Usuario logado) {
    if (!(logado.ehDono(id) || logado.ehAdmin())) {
      throw new AccessDeniedException("Usuário não possui permissão");
    }

    var cliente = repository.findById(id).orElseThrow();

    return new DetalhamentoClienteDto(cliente);
  }

  @Transactional
  public DetalhamentoClienteDto reativarCliente(Long id) {
    var cliente = repository.findById(id).orElseThrow();

    if (cliente.getAtivo()) {
      throw new RegraDeNegocioException("O Cliente já está ativo.");
    }

    cliente.ativar();
    usuarioService.reativarUsuario(id);

    return new DetalhamentoClienteDto(cliente);
  }

}
