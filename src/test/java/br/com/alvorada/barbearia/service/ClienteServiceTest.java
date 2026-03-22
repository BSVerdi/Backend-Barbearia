package br.com.alvorada.barbearia.service;

import br.com.alvorada.barbearia.dto.cliente.AtualizacaoDadosClienteDto;
import br.com.alvorada.barbearia.dto.cliente.CadastroClienteDto;
import br.com.alvorada.barbearia.dto.cliente.ClienteDto;
import br.com.alvorada.barbearia.dto.cliente.DetalhamentoClienteDto;
import br.com.alvorada.barbearia.exception.RegraDeNegocioException;
import br.com.alvorada.barbearia.model.Cliente;
import br.com.alvorada.barbearia.repository.ClienteRepository;
import br.com.alvorada.barbearia.security.service.TokenService;
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

import static br.com.alvorada.barbearia.Factory.ClienteFactory.criarClienteComTodosOsCampos;
import static br.com.alvorada.barbearia.Factory.UsuarioFactory.criarUsuarioAdminComTodosOsCampos;
import static br.com.alvorada.barbearia.Factory.UsuarioFactory.criarUsuarioComTodosOsCampos;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

  @InjectMocks
  private ClienteService clienteService;

  @Mock
  private ClienteRepository clienteRepository;

  @Mock
  private UsuarioService usuarioService;

  @Mock
  private TokenService tokenService;

  @Captor
  private ArgumentCaptor<Cliente> clienteCaptor;

  @Nested
  @DisplayName("Quando cadastrar um cliente")
  class cadastrar {

    @Nested
    @DisplayName("Entao deve executar com sucesso")
    class sucesso {

      @Test
      @DisplayName("Dado um cliente com todos os campos e email e telefone ausentes no banco")
      public void teste01() {
        var dto = new CadastroClienteDto(
                "exemplo",
                "exemplo@email.com",
                "exemploSenha",
                "123456789"
        );

        given(clienteRepository.existsByEmailOrTelefone(any(), any()))
                .willReturn(false);

        var usuario = criarUsuarioComTodosOsCampos();
        given(usuarioService.salvarUsuario(any(), any(), any(), any()))
                .willReturn(usuario);

        given(tokenService.gerarToken(any())).willReturn("token");
        given(tokenService.gerarRefreshToken(any())).willReturn("token");

        given(clienteRepository.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        clienteService.cadastrar(dto);

        then(clienteRepository).should().save(clienteCaptor.capture());
        var cliente = clienteCaptor.getValue();


        Assertions.assertNotNull(cliente);
        Assertions.assertEquals(usuario.getId(), cliente.getId());
        Assertions.assertEquals(dto.nome(), cliente.getNome());
        Assertions.assertEquals(dto.email(), cliente.getEmail());
        Assertions.assertEquals(dto.telefone(), cliente.getTelefone());
        Assertions.assertTrue(cliente.getAtivo());
      }

    }

    @Nested
    @DisplayName("Entao deve retornar erro")
    class falha {

      @Test
      @DisplayName("Dado um cliente com todos os campos e email ou telefone presentes no banco")
      public void teste02() {
        var dto = new CadastroClienteDto(
                "exemplo",
                "exemplo@email.com",
                "exemploSenha",
                "123456789"
        );

        given(clienteRepository.existsByEmailOrTelefone(any(), any()))
                .willReturn(true);

        Assertions.assertThrows(
                RegraDeNegocioException.class,
                () -> clienteService.cadastrar(dto)
        );
      }

    }

  }

  @Nested
  @DisplayName("Quando listar clientes")
  class ListarClientes {

    @Test
    @DisplayName("Deveria retornar todos os clientes")
    public void teste03() {
      var cliente1 = criarClienteComTodosOsCampos();
      var cliente2 = criarClienteComTodosOsCampos();
      var cliente3 = criarClienteComTodosOsCampos();

      var clientes = List.of(cliente1, cliente2, cliente3);

      given(clienteRepository.findAll()).willReturn(clientes);

      var usuariosEsperados = clientes.stream()
              .map(ClienteDto::new)
              .toList();

      var usuariosRetornados = clienteService.listarClientes();

      Assertions.assertEquals(usuariosEsperados, usuariosRetornados);
    }

  }

  @Nested
  @DisplayName("Quando deletar um cliente")
  class DeletarCliente {

    @Nested
    @DisplayName("Deveria desativar com sucesso quando")
    class Sucesso {

      @Test
      @DisplayName("O usuario logado é o cliente mas não é admin")
      public void teste04() {
        var cliente = criarClienteComTodosOsCampos();
        var usuario = criarUsuarioComTodosOsCampos();

        given(clienteRepository.findById(cliente.getId()))
                .willReturn(Optional.of(cliente));

        Assertions.assertDoesNotThrow(
                () -> clienteService.deletarCliente(usuario.getId(), usuario)
        );

        Assertions.assertFalse(cliente.getAtivo());
      }

      @Test
      @DisplayName("O usuario logado não é o cliente mas é admin")
      public void teste05() {
        var cliente = criarClienteComTodosOsCampos();
        var usuario = criarUsuarioAdminComTodosOsCampos();

        given(clienteRepository.findById(cliente.getId()))
                .willReturn(Optional.of(cliente));

        Assertions.assertDoesNotThrow(
                () -> clienteService.deletarCliente(cliente.getId(), usuario)
        );
        Assertions.assertFalse(cliente.getAtivo());
      }

      @Test
      @DisplayName("O cliente é desativado")
      public void teste06() {
        var cliente = criarClienteComTodosOsCampos();
        var usuario = criarUsuarioAdminComTodosOsCampos();

        given(clienteRepository.findById(cliente.getId()))
                .willReturn(Optional.of(cliente));

        clienteService.deletarCliente(cliente.getId(), usuario);

        Assertions.assertFalse(cliente.getAtivo());
      }

    }

    @Nested
    @DisplayName("Deveria retornar erro")
    class Falha {

      @Test
      @DisplayName("Dado um usuario logado não for o dono ou admin")
      public void teste07() {
        var cliente = criarClienteComTodosOsCampos();
        var usuario = criarUsuarioComTodosOsCampos();

        Assertions.assertThrows(
                AccessDeniedException.class,
                () -> clienteService.deletarCliente(cliente.getId() + 1L, usuario)
        );
      }

      @Test
      @DisplayName("Dado um cliente já estivar desativado")
      public void teste08() {
        var cliente = criarClienteComTodosOsCampos();
        var usuario = criarUsuarioComTodosOsCampos();

        cliente.desativar();

        given(clienteRepository.findById(cliente.getId()))
                .willReturn(Optional.of(cliente));

        Assertions.assertThrows(
                RegraDeNegocioException.class,
                () -> clienteService.deletarCliente(cliente.getId(), usuario)
        );
      }

      @Test
      @DisplayName("Dado um cliente não existir no banco de dados")
      public void teste09() {
        var cliente = criarClienteComTodosOsCampos();
        var usuario = criarUsuarioComTodosOsCampos();

        given(clienteRepository.findById(cliente.getId()))
                .willReturn(Optional.empty());

        Assertions.assertThrows(
                NoSuchElementException.class,
                () -> clienteService.deletarCliente(cliente.getId(), usuario)
        );
      }

    }

  }

  @Nested
  @DisplayName("Quando atualizar os dados de um cliente")
  class AtualizarDadosCliente {

    @Nested
    @DisplayName("Deveria atualizar com sucesso")
    class Sucesso {

      @Test
      @DisplayName("Dado que retorne o cliente atualizado detalhado")
      public void teste10() {
        var cliente = criarClienteComTodosOsCampos();
        var usuario = criarUsuarioComTodosOsCampos();
        var dto = new AtualizacaoDadosClienteDto(1L, "novoNome", "10987654321");

        given(clienteRepository.findById(cliente.getId()))
                .willReturn(Optional.of(cliente));


        var clienteEsperado = criarClienteComTodosOsCampos();
        clienteEsperado.atualizarDados(dto);

        var clienteRetornado = clienteService.atualizarDados(dto, usuario);

        Assertions.assertEquals(
                clienteRetornado,
                new DetalhamentoClienteDto(clienteEsperado)
        );
      }

      @Test
      @DisplayName("Dado que atualize os dados do cliente")
      public void teste11() {
        var cliente = criarClienteComTodosOsCampos();
        var usuario = criarUsuarioComTodosOsCampos();
        var dto = new AtualizacaoDadosClienteDto(1L, "novoNome", "10987654321");

        given(clienteRepository.findById(cliente.getId()))
                .willReturn(Optional.of(cliente));

        clienteService.atualizarDados(dto, usuario);

        Assertions.assertEquals(dto.id(), cliente.getId());
        Assertions.assertEquals(dto.nome(), cliente.getNome());
        Assertions.assertEquals(dto.telefone(), cliente.getTelefone());
      }

    }

    @Nested
    @DisplayName("Deveria retornar erro")
    class Falha {

      @Test
      @DisplayName("Dado um usuario que não é o cliente")
      public void teste12() {
        var usuario = criarUsuarioComTodosOsCampos();
        var dto = new AtualizacaoDadosClienteDto(usuario.getId() + 1L, "novoNome", "10987654321");

        Assertions.assertThrows(
                AccessDeniedException.class,
                () -> clienteService.atualizarDados(dto, usuario)
        );
      }

      @Test
      @DisplayName("Dado um cliente que não existe no banco de dados")
      public void teste13() {
        var dto = new AtualizacaoDadosClienteDto(1L, "novoNome", "10987654321");
        var usuario = criarUsuarioComTodosOsCampos();

        given(clienteRepository.findById(1L)).willReturn(Optional.empty());

        Assertions.assertThrows(
                NoSuchElementException.class,
                () -> clienteService.atualizarDados(dto, usuario)
        );
      }

    }

  }

  @Nested
  @DisplayName("Quando detalhar um cliente")
  class DetalharCliente {

    @Nested
    @DisplayName("Deveria detalhar com sucesso")
    class Sucesso {

      @Test
      @DisplayName("Dado um usuario que é admin")
      public void teste14() {
        var cliente = criarClienteComTodosOsCampos();
        var usuario = criarUsuarioAdminComTodosOsCampos();

        given(clienteRepository.findById(cliente.getId()))
                .willReturn(Optional.of(cliente));

        Assertions.assertDoesNotThrow(
                () -> clienteService.detalharCliente(cliente.getId(), usuario)
        );
      }

      @Test
      @DisplayName("Dado um usuario que é o próprio cliente")
      public void teste15() {
        var cliente = criarClienteComTodosOsCampos();
        var usuario = criarUsuarioComTodosOsCampos();

        given(clienteRepository.findById(cliente.getId()))
                .willReturn(Optional.of(cliente));

        Assertions.assertDoesNotThrow(
                () -> clienteService.detalharCliente(cliente.getId(), usuario)
        );
      }

      @Test
      @DisplayName("Dado que retorne o cliente detalhado com todos os dados")
      public void teste16() {
        var cliente = criarClienteComTodosOsCampos();
        var usuario = criarUsuarioComTodosOsCampos();

        given(clienteRepository.findById(cliente.getId()))
                .willReturn(Optional.of(cliente));

        var clienteRetornardo = clienteService
                .detalharCliente(cliente.getId(), usuario);

        Assertions.assertEquals(
                clienteRetornardo,
                new DetalhamentoClienteDto(cliente)
        );
      }

    }

    @Nested
    @DisplayName("Deveria retornar erro")
    class Falha {

      @Test
      @DisplayName("Dado um usuario que não é admin e nem o cliente")
      public void teste17() {
        var usuario = criarUsuarioComTodosOsCampos();

        Assertions.assertThrows(
                AccessDeniedException.class,
                () -> clienteService.detalharCliente(usuario.getId() + 1L, usuario)
        );
      }

      @Test
      @DisplayName("Dado um cliente que não está no banco de dados")
      public void teste18() {
        var usuario = criarUsuarioComTodosOsCampos();

        given(clienteRepository.findById(1L)).willReturn(Optional.empty());

        Assertions.assertThrows(
                NoSuchElementException.class,
                () -> clienteService.detalharCliente(1L, usuario)
        );
      }

    }

  }

  @Nested
  @DisplayName("Quando reativar um cliente")
  class ReativarCliente {

    @Nested
    @DisplayName("Devera reativar com sucesso")
    class Sucesso {

      @Test
      @DisplayName("Dado que o cliente seja reativado")
      public void teste19() {
        var cliente = criarClienteComTodosOsCampos();
        cliente.desativar();

        given(clienteRepository.findById(cliente.getId()))
                .willReturn(Optional.of(cliente));

        clienteService.reativarCliente(cliente.getId());

        Assertions.assertTrue(cliente.getAtivo());
      }

      @Test
      @DisplayName("Dado que retornou o detalhamento do cliente com todos os dados")
      public void teste20() {
        var cliente = criarClienteComTodosOsCampos();
        cliente.desativar();

        given(clienteRepository.findById(cliente.getId()))
                .willReturn(Optional.of(cliente));

        var clienteRetornardo = clienteService.reativarCliente(cliente.getId());

        Assertions.assertEquals(
                clienteRetornardo,
                new DetalhamentoClienteDto(cliente)
        );
      }

    }

    @Nested
    @DisplayName("Devera retornar Erro")
    class Falha {

      @Test
      @DisplayName("Dado que o cliente não exites no banco de dados")
      public void teste21() {
        given(clienteRepository.findById(1L)).willReturn(Optional.empty());

        Assertions.assertThrows(
                NoSuchElementException.class,
                () -> clienteService.reativarCliente(1L)
        );
      }

      @Test
      @DisplayName("Dado que o cliente ja esta ativo")
      public void teste22() {
        var cliente = criarClienteComTodosOsCampos();

        given(clienteRepository.findById(cliente.getId()))
                .willReturn(Optional.of(cliente));

        Assertions.assertThrows(
                RegraDeNegocioException.class,
                () -> clienteService.reativarCliente(cliente.getId())
        );
      }

    }

  }

}
