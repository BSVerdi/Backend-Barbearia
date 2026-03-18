package br.com.alvorada.barbearia.service;

import br.com.alvorada.barbearia.dto.cliente.CadastroClienteDto;
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
      public void teste01() {
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

}