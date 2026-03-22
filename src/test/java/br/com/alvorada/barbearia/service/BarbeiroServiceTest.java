package br.com.alvorada.barbearia.service;

import br.com.alvorada.barbearia.dto.barbeiro.AtualizacaoDadosBarbeiroDto;
import br.com.alvorada.barbearia.dto.barbeiro.BarbeiroDto;
import br.com.alvorada.barbearia.dto.barbeiro.DetalhamentoBarbeiroDto;
import br.com.alvorada.barbearia.exception.RegraDeNegocioException;
import br.com.alvorada.barbearia.model.Barbeiro;
import br.com.alvorada.barbearia.repository.BarbeiroRepository;
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
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static br.com.alvorada.barbearia.Factory.BarbeiroFactory.*;
import static br.com.alvorada.barbearia.Factory.UsuarioFactory.criarUsuarioAdminComTodosOsCampos;
import static br.com.alvorada.barbearia.Factory.UsuarioFactory.criarUsuarioComTodosOsCampos;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class BarbeiroServiceTest {

  @InjectMocks
  private BarbeiroService barbeiroService;

  @Mock
  private BarbeiroRepository barbeiroRepository;

  @Mock
  UsuarioService usuarioService;

  @Mock
  AgendamentoService agendamentoService;

  @Captor
  private ArgumentCaptor<Barbeiro> barbeiroCaptor;

  @Nested
  @DisplayName("Quando listar os barbeiros")
  class ListarBarbeiros {

    @Test
    @DisplayName("Deveria listar todos os barbeiros")
    public void teste01() {
      var barbeiro1 = criarBarbeiroComTodosOsCampos();
      var barbeiro2 = criarBarbeiroComTodosOsCampos();

      var barbeiros = List.of(barbeiro1, barbeiro2);

      given(barbeiroRepository.findAll()).willReturn(barbeiros);

      var barbeirosEsperados = barbeiros.stream().map(BarbeiroDto::new).toList();
      var barbeirosRetornados = barbeiroService.listarBarbeiros();

      Assertions.assertEquals(barbeirosEsperados, barbeirosRetornados);
    }

  }

  @Nested
  @DisplayName("Quando listar os barbeiros ativos")
  class ListarBarbeirosAtivos {

    @Test
    @DisplayName("Deveria listar todos os barbeiros ativos")
    public void teste02() {
      var barbeiro1 = criarBarbeiroComTodosOsCampos();
      var barbeiro2 = criarBarbeiroComTodosOsCampos();

      var barbeiros = List.of(barbeiro1, barbeiro2);

      given(barbeiroRepository.findByAtivoTrue()).willReturn(barbeiros);

      var barbeirosEsperados = barbeiros.stream().map(BarbeiroDto::new).toList();
      var barbeirosRetornados = barbeiroService.listarBarbeiros();

      Assertions.assertEquals(barbeirosEsperados, barbeirosRetornados);
    }

  }

  @Nested
  @DisplayName("Quando detalhar um barbeiro")
  class DetalharBarbeiro {

    @Test
    @DisplayName("Deveria retornar o barbeiro com todos os dados")
    public void teste03() {
      var barbeiro = criarBarbeiroComTodosOsCampos();

      given(barbeiroRepository.findById(any()))
              .willReturn(Optional.of(barbeiro));

      var barbeiroEsperado = new DetalhamentoBarbeiroDto(barbeiro);
      var barbeiroRetornado = barbeiroService.detalharBarbeiro(barbeiro.getId());

      Assertions.assertEquals(barbeiroEsperado, barbeiroRetornado);
    }

    @Test
    @DisplayName("Deveria retornar erro dado que o barbeiro não exista no banco de dados")
    public void teste04() {
      given(barbeiroRepository.findById(any())).willReturn(Optional.empty());

      Assertions.assertThrows(
              NoSuchElementException.class,
              () -> barbeiroService.detalharBarbeiro(any())
      );
    }

  }

  @Nested
  @DisplayName("Quando cadastrar um barbeiro")
  class Cadastrar {

    @Nested
    @DisplayName("Deveria cadastrar com sucesso")
    class Sucesso {

      @Test
      @DisplayName("Dado um barbeiro com todos os campos")
      public void teste05() {
        var dto = criarCadastroBarbeiroComTodosOsCampos();
        var barbeiro = criarBarbeiroComTodosOsCampos();
        var usuario = criarUsuarioComTodosOsCampos();

        given(barbeiroRepository.existsByEmailOrTelefone(any(), any()))
                .willReturn(false);

        given(usuarioService.salvarUsuario(any(), any(), any(), any()))
                .willReturn(usuario);

        given(barbeiroRepository.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        barbeiroService.cadastrar(dto);

        then(barbeiroRepository).should().save(barbeiroCaptor.capture());
        var barbeiroSalvo = barbeiroCaptor.getValue();

        Assertions.assertNotNull(barbeiroSalvo);
        Assertions.assertEquals(barbeiro.getId(), barbeiroSalvo.getId());
        Assertions.assertEquals(barbeiro.getEmail(), barbeiroSalvo.getEmail());
        Assertions.assertEquals(barbeiro.getNome(), barbeiroSalvo.getNome());
        Assertions.assertEquals(barbeiro.getTelefone(), barbeiroSalvo.getTelefone());
        Assertions.assertTrue(barbeiroSalvo.getAtivo());
      }

      @Test
      @DisplayName("Dado que retorne o detalhamento do barbeiro cadastrado")
      public void teste06() {
        var dto = criarCadastroBarbeiroComTodosOsCampos();
        var barbeiro = criarBarbeiroComTodosOsCampos();
        var usuario = criarUsuarioComTodosOsCampos();

        given(barbeiroRepository.existsByEmailOrTelefone(any(), any()))
                .willReturn(false);

        given(usuarioService.salvarUsuario(any(), any(), any(), any()))
                .willReturn(usuario);

        given(barbeiroRepository.save(any())).willReturn(barbeiro);

        var barbeiroEsperado = new DetalhamentoBarbeiroDto(barbeiro);
        var barbeiroRetornado = barbeiroService.cadastrar(dto);

        Assertions.assertEquals(barbeiroEsperado, barbeiroRetornado);
      }

    }

    @Nested
    @DisplayName("Deveria retornar erro")
    class Falha {

      @Test
      @DisplayName("Dado que o email ou telefone já está cadastrado")
      public void teste07() {
        var dto = criarCadastroBarbeiroComTodosOsCampos();

        given(barbeiroRepository.existsByEmailOrTelefone(any(), any()))
                .willReturn(true);

        Assertions.assertThrows(
                RegraDeNegocioException.class,
                () -> barbeiroService.cadastrar(dto)
        );
      }

    }

  }

  @Nested
  @DisplayName("Quando atualizar os dados de um barbeiro")
  class AtualizarDados {

    @Nested
    @DisplayName("Deveria atualizar com sucesso")
    class Sucesso {

      @Test
      @DisplayName("Dado que atualize os dados do barbeiro")
      public void teste08() {
        var dto = criarAtualizacaoBarbeiroComTodosOsCampos();
        var usuario = criarUsuarioComTodosOsCampos();
        var barbeiro = criarBarbeiroComTodosOsCampos();

        given(barbeiroRepository.findById(any()))
                .willReturn(Optional.of(barbeiro));

        barbeiroService.atualizarDados(dto, usuario);

        Assertions.assertEquals(dto.nome(), barbeiro.getNome());
        Assertions.assertEquals(dto.telefone(), barbeiro.getTelefone());
        Assertions.assertEquals(dto.horarioInicio(), barbeiro.getHorarioInicio());
        Assertions.assertEquals(dto.horarioFim(), barbeiro.getHorarioFim());
      }

      @Test
      @DisplayName("Dado que retorne os dados atualizados do barbeiro detalhado")
      public void teste09() {
        var dto = criarAtualizacaoBarbeiroComTodosOsCampos();
        var usuario = criarUsuarioComTodosOsCampos();
        var barbeiro = criarBarbeiroComTodosOsCampos();

        given(barbeiroRepository.findById(any()))
                .willReturn(Optional.of(barbeiro));

        barbeiro.atualizarDados(dto);

        var barbeiroEsperado = new DetalhamentoBarbeiroDto(barbeiro);
        var barbeiroRetornado = barbeiroService.atualizarDados(dto, usuario);

        Assertions.assertEquals(barbeiroEsperado, barbeiroRetornado);
      }

    }

    @Nested
    @DisplayName("Deveria retornar erro")
    class Falha {

      @Test
      @DisplayName("Dado que o usuario não é o barbeiro nem admin")
      public void teste10() {
        var usuario = criarUsuarioComTodosOsCampos();
        var dto = new AtualizacaoDadosBarbeiroDto(
                usuario.getId() + 1L,
                null,
                null,
                null,
                null
        );


        Assertions.assertThrows(
                AccessDeniedException.class,
                () -> barbeiroService.atualizarDados(dto, usuario)
        );
      }

      @Test
      @DisplayName("Dado que o barbeiro não existe no banco de dados")
      public void teste11() {
        var usuario = criarUsuarioComTodosOsCampos();
        var dto = criarAtualizacaoBarbeiroComTodosOsCampos();

        given(barbeiroRepository.findById(any()))
                .willReturn(Optional.empty());

        Assertions.assertThrows(
                NoSuchElementException.class,
                () -> barbeiroService.atualizarDados(dto, usuario)
        );
      }

    }

  }

  @Nested
  @DisplayName("Quando deletar um barbeiro")
  class DeletarBarbeiro {

    @Nested
    @DisplayName("Deveria desativar com sucesso")
    class Sucesso {

      @Test
      @DisplayName("Dado que o usuario logado é o barbeiro")
      public void teste12() {
        var usuario = criarUsuarioComTodosOsCampos();
        var barbeiro = criarBarbeiroComTodosOsCampos();

        given(barbeiroRepository.findById(any()))
                .willReturn(Optional.of(barbeiro));

        Assertions.assertDoesNotThrow(
                () -> barbeiroService.deletarBarbeiro(usuario.getId(), usuario)
        );
      }

      @Test
      @DisplayName("Dado que o usuario logado é admin")
      public void teste13() {
        var usuario = criarUsuarioAdminComTodosOsCampos();
        var barbeiro = criarBarbeiroComTodosOsCampos();

        given(barbeiroRepository.findById(any()))
                .willReturn(Optional.of(barbeiro));

        Assertions.assertDoesNotThrow(
                () -> barbeiroService.deletarBarbeiro(usuario.getId() + 1L, usuario)
        );
      }

      @Test
      @DisplayName("Dado que o barbeiro é desativado")
      public void teste14() {
        var usuario = criarUsuarioComTodosOsCampos();
        var barbeiro = criarBarbeiroComTodosOsCampos();

        given(barbeiroRepository.findById(any()))
                .willReturn(Optional.of(barbeiro));

        barbeiroService.deletarBarbeiro(barbeiro.getId(), usuario);

        Assertions.assertFalse(barbeiro.getAtivo());
      }

    }

    @Nested
    @DisplayName("Deveria retornar erro")
    class Falha {

      @Test
      @DisplayName("Dado que o barbeiro não exista no banco de dados")
      public void teste15() {
        var usuario = criarUsuarioComTodosOsCampos();

        given(barbeiroRepository.findById(any())).willReturn(Optional.empty());

        Assertions.assertThrows(
                NoSuchElementException.class,
                () -> barbeiroService.deletarBarbeiro(usuario.getId(), usuario)
        );
      }

      @Test
      @DisplayName("Dado que o usuário logado não é admin ou o propio barbeiro")
      public void teste16() {
        var usuario = criarUsuarioComTodosOsCampos();

        Assertions.assertThrows(
                AccessDeniedException.class,
                () -> barbeiroService.deletarBarbeiro(usuario.getId() + 1L, usuario)
        );
      }

    }

  }

}