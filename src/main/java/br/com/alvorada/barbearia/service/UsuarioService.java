package br.com.alvorada.barbearia.service;

import br.com.alvorada.barbearia.dto.usuario.AtualizacaoUsuarioPerfilDto;
import br.com.alvorada.barbearia.dto.usuario.DetalhamentoUsuarioDto;
import br.com.alvorada.barbearia.model.Perfil;
import br.com.alvorada.barbearia.model.Usuario;
import br.com.alvorada.barbearia.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService implements UserDetailsService {

  private final UsuarioRepository repository;
  private final PasswordEncoder passwordEncoder;

  public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
    this.repository = repository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return repository.findByEmailIgnoreCase(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));

  }

  @Transactional
  public Usuario salvarUsuario(
          String nome, String email, String senha, Perfil perfil
  ) {
    var senhaCriptografada = passwordEncoder.encode(senha);

    return repository.save(new Usuario(nome, email, senhaCriptografada, perfil));
  }

  @Transactional
  public List<DetalhamentoUsuarioDto> listarUsuarios() {
    return repository.findAll().stream()
            .map(DetalhamentoUsuarioDto::new)
            .toList();

  }

  @Transactional
  public DetalhamentoUsuarioDto detalharUsuario(Long id) {
    var usuario = repository.findById(id).orElseThrow();

    return new DetalhamentoUsuarioDto(usuario);
  }

  @Transactional
  public DetalhamentoUsuarioDto atualizarPerfil(AtualizacaoUsuarioPerfilDto dto) {
    var usuario = repository.findById(dto.id()).orElseThrow();

    usuario.atualizarPerfil(dto.perfil());

    return new DetalhamentoUsuarioDto(usuario);
  }

  @Transactional
  public void deletarUsuario(Long id) {
    var usuario = repository.findById(id).orElseThrow();

    usuario.desativar();
  }

  @Transactional
  public void atualizarDados(Long id, String nome) {
    var usuario = repository.findById(id).orElseThrow();

    usuario.atualizarNome(nome);
  }

  @Transactional
  public void reativarUsuario(Long id) {
    var usuario = repository.findById(id).orElseThrow();

    usuario.ativar();
  }

}
