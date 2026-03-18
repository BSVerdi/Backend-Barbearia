package br.com.alvorada.barbearia.service;

import br.com.alvorada.barbearia.dto.usuario.AtualizacaoUsuarioPerfilDto;
import br.com.alvorada.barbearia.dto.usuario.DetalhamentoUsuarioDto;
import br.com.alvorada.barbearia.model.Perfil;
import br.com.alvorada.barbearia.model.Usuario;
import br.com.alvorada.barbearia.repository.UsuarioRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static br.com.alvorada.barbearia.Factory.UsuarioFactory.criarUsuarioComTodosOsCampos;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

  @InjectMocks
  private UsuarioService usuarioService;

  @Mock
  private UsuarioRepository usuarioRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Captor
  private ArgumentCaptor<Usuario> usuarioCaptor;

  @Nested
  @DisplayName("Quando salvar o usuario")
  class SalvarUsuarios {

    @Test
    @DisplayName("Deveria salvar o usuario com todos os dados correto")
    public void teste01() {
      var nome = "teste";
      var email = "teste@email.com";
      var senha = "exemploSenha";
      var perfil = Perfil.CLIENTE;

      given(passwordEncoder.encode(senha)).willReturn(senha);

      usuarioService.salvarUsuario(nome, email, senha, perfil);

      then(usuarioRepository).should().save(usuarioCaptor.capture());
      var usuarioSalvo = usuarioCaptor.getValue();

      Assertions.assertEquals(nome, usuarioSalvo.getNome());
      Assertions.assertEquals(email, usuarioSalvo.getEmail());
      Assertions.assertEquals(senha, usuarioSalvo.getSenha());
      Assertions.assertEquals(perfil, usuarioSalvo.getPerfil());
      Assertions.assertTrue(usuarioSalvo.getAtivo());
    }

  }

  @Nested
  @DisplayName("Quando listar usuarios")
  class ListarUsuarios {

    @Test
    @DisplayName("Deveria retornar todos os usuários")
    public void teste02() {
      var usuario1 = criarUsuarioComTodosOsCampos();
      var usuario2 = criarUsuarioComTodosOsCampos();
      var usuario3 = criarUsuarioComTodosOsCampos();

      var usuarios = List.of(usuario1, usuario2, usuario3);

      given(usuarioRepository.findAll()).willReturn(usuarios);

      var usuariosEsperados = usuarios.stream()
              .map(DetalhamentoUsuarioDto::new)
              .toList();

      var usuariosListados = usuarioService.listarUsuarios();

      Assertions.assertEquals(usuariosEsperados.size(), usuariosListados.size());
      Assertions.assertTrue(usuariosEsperados.containsAll(usuariosListados));
    }

  }

  @Nested
  @DisplayName("Quando detalhar um usuario")
  class DetalharUsuario {

    @Test
    @DisplayName("Deveria retornar o usuario correto")
    public void teste03() {
      var usuario = criarUsuarioComTodosOsCampos();

      given(usuarioRepository.findById(usuario.getId()))
              .willReturn(Optional.of(usuario));

      var usuarioEsperado = new DetalhamentoUsuarioDto(usuario);

      var usuarioRetornado = usuarioService.detalharUsuario(usuario.getId());

      Assertions.assertEquals(usuarioEsperado, usuarioRetornado);
    }

  }

  @Nested
  @DisplayName("Quando atualizar o perfil de um usuario")
  class AtualizarPerfil {

    @Test
    @DisplayName("Deveria retornar o usuario com o perfil atualizado")
    public void teste04() {
      var usuario = criarUsuarioComTodosOsCampos();

      given(usuarioRepository.findById(usuario.getId()))
              .willReturn(Optional.of(usuario));

      usuario.atualizarPerfil(Perfil.BARBEIRO);
      var usuarioEsperado = new DetalhamentoUsuarioDto(usuario);

      var usuarioRetornado = usuarioService.atualizarPerfil(
              new AtualizacaoUsuarioPerfilDto(usuario.getId(), Perfil.BARBEIRO)
      );

      Assertions.assertEquals(usuarioEsperado, usuarioRetornado);
    }

  }

  @Nested
  @DisplayName("Quando deletar um usuario")
  class DeletarUsuario {

    @Test
    @DisplayName("Deveria desativar o usuario")
    public void teste05() {
      var usuario = criarUsuarioComTodosOsCampos();

      given(usuarioRepository.findById(usuario.getId()))
              .willReturn(Optional.of(usuario));

      usuarioService.deletarUsuario(usuario.getId());

      Assertions.assertEquals(false, usuario.getAtivo());
    }

  }

  @Nested
  @DisplayName("Quando atualizar os dados de um usuario")
  class AtualizarDadosUsuario {

    @Test
    @DisplayName("Deveria atualizar o nome do usuario")
    public void teste06() {
      var usuario = criarUsuarioComTodosOsCampos();
      var novosDados = "novoNome";

      given(usuarioRepository.findById(usuario.getId()))
              .willReturn(Optional.of(usuario));

      usuarioService.atualizarDados(usuario.getId(), novosDados);

      Assertions.assertEquals(novosDados, usuario.getNome());
    }

  }

  @Nested
  @DisplayName("Quando reativar um usuario")
  class ReativarUsuario {

    @Test
    @DisplayName("Deveria reativar o usuario desativado")
    public void teste07() {
      var usuario = criarUsuarioComTodosOsCampos();
      usuario.desativar();

      given(usuarioRepository.findById(usuario.getId()))
              .willReturn(Optional.of(usuario));

      usuarioService.reativarUsuario(usuario.getId());

      Assertions.assertEquals(true, usuario.getAtivo());
    }

  }

}