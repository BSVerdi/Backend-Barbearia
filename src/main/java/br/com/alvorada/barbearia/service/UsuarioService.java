package br.com.alvorada.barbearia.service;

import br.com.alvorada.barbearia.dto.CadastroUsuarioDto;
import br.com.alvorada.barbearia.dto.DetalhamentoUsuarioDto;
import br.com.alvorada.barbearia.model.PerfilNome;
import br.com.alvorada.barbearia.model.Usuario;
import br.com.alvorada.barbearia.repository.PerfilRepository;
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
  private final PerfilRepository perfilRepository;

  public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder, PerfilRepository perfilRepository) {
    this.repository = repository;
    this.passwordEncoder = passwordEncoder;
    this.perfilRepository = perfilRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return repository.findByEmailIgnoreCase(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));
  }

  @Transactional
  public Usuario cadastrar(CadastroUsuarioDto dto) {
    var senhaCriptografada = passwordEncoder.encode(dto.senha());

    var perfil = perfilRepository.findByNome(PerfilNome.CLIENTE);
    var usuario = new Usuario(dto, senhaCriptografada, perfil);

    return repository.save(usuario);
  }

  public List<DetalhamentoUsuarioDto> listarUsuarios() {
    return repository.findAll().stream()
            .map(DetalhamentoUsuarioDto::new)
            .toList();
  }

  public DetalhamentoUsuarioDto detalharUsuario(Long id) {
    var usuario = repository.findById(id).orElseThrow();

    return new DetalhamentoUsuarioDto(usuario);
  }
}
