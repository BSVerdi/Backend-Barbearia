package br.com.alvorada.barbearia.controller;

import br.com.alvorada.barbearia.exception.RegraDeNegocioException;
import br.com.alvorada.barbearia.service.ClienteService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;

import static br.com.alvorada.barbearia.Factory.ClienteFactory.criarDetalhamentoCadastroClienteDto;
import static br.com.alvorada.barbearia.Factory.ClienteFactory.criarDetalhamentoClienteDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
class ClienteControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockitoBean
  private ClienteService clienteService;

  @Nested
  @DisplayName("POST /clientes/cadastrar")
  class Cadastrar {

    @Test
    @DisplayName("Deveria retornar código 201 quandos os dados estão validos")
    public void teste01() throws Exception {
      var detalhamentoCadastroDto = criarDetalhamentoCadastroClienteDto();
      var json = """
              {
                "nome": "exemplo",
                "email": "exemplo@email.com",
                "senha": "exemplo",
                "telefone": "99999999999"
              }
              """;

      given(clienteService.cadastrar(any())).willReturn(detalhamentoCadastroDto);

      var response = mvc.perform(
              post("/clientes/cadastrar")
                      .content(json)
                      .contentType(MediaType.APPLICATION_JSON)
      ).andReturn().getResponse();

      Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    }

    @Test
    @DisplayName("Deveria retornar código 400 quando os dados estiverem incompletos")
    public void teste02() throws Exception {
      var detalhamentoCadastroDto = criarDetalhamentoCadastroClienteDto();
      var json = "{}";

      given(clienteService.cadastrar(any())).willReturn(detalhamentoCadastroDto);

      var response = mvc.perform(
              post("/clientes/cadastrar")
                      .content(json)
                      .contentType(MediaType.APPLICATION_JSON)
      ).andReturn().getResponse();

      Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    @DisplayName("Deveria retornar código 400 quando o cliente já existe no banco de dados")
    public void teste03() throws Exception {
      var json = """
              {
                "nome": "exemplo",
                "email": "exemplo@email.com",
                "senha": "exemplo",
                "telefone": "99999999999"
              }
              """;

      given(clienteService.cadastrar(any()))
              .willThrow(RegraDeNegocioException.class);

      var response = mvc.perform(
              post("/clientes/cadastrar")
                      .content(json)
                      .contentType(MediaType.APPLICATION_JSON)
      ).andReturn().getResponse();

      Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

  }

  @Nested
  @DisplayName("GET /clientes/listar")
  class ListarClientes {

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Deveria retornar código 200 quando estiver autenticado como admin")
    public void teste04() throws Exception {
      var response = mvc.perform(
              get("/clientes")
      ).andReturn().getResponse();

      Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    @DisplayName("Deveria retornar código 403 quando não estiver autenticado")
    public void teste05() throws Exception {
      var response = mvc.perform(
              get("/clientes")
      ).andReturn().getResponse();

      Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

  }

  @Nested
  @DisplayName("GET /clientes/{id}")
  class DetalharCliente {

    @BeforeEach
    public void setup() {
      var detalhamentoClienteDto = criarDetalhamentoClienteDto();

      given(clienteService.detalharCliente(any(), any()))
              .willReturn(detalhamentoClienteDto);
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("Deveria retornar código 200 quando estiver autenticado como cliente")
    public void teste06() throws Exception {
      var response = mvc.perform(
              get("/clientes/1")
      ).andReturn().getResponse();

      Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Deveria retornar código 200 quando estiver autenticado como admin")
    public void teste07() throws Exception {
      var response = mvc.perform(
              get("/clientes/1")
      ).andReturn().getResponse();

      Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    @WithMockUser(roles = "BARBEIRO")
    @DisplayName("Deveria retornar código 403 quando estiver autenticado como barbeiro")
    public void teste08() throws Exception {
      var response = mvc.perform(
              get("/clientes/1")
      ).andReturn().getResponse();

      Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    @DisplayName("Deveria retornar código 403 quando não estiver autenticado")
    public void teste09() throws Exception {
      var response = mvc.perform(
              get("/clientes/1")
      ).andReturn().getResponse();

      Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("Deveria retornar código 403 quando estiver autenticado como cliente mas não for o dono da conta")
    public void teste10() throws Exception {
      given(clienteService.detalharCliente(any(), any()))
              .willThrow(AccessDeniedException.class);

      var response = mvc.perform(
              get("/clientes/1")
      ).andReturn().getResponse();

      Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Deveria retornar código 404 quando o cliente não existir no banco de dados")
    public void teste11() throws Exception {
      given(clienteService.detalharCliente(any(), any()))
              .willThrow(NoSuchElementException.class);

      var response = mvc.perform(
              get("/clientes/1")
      ).andReturn().getResponse();

      Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

  }

  @Nested
  @DisplayName("PATCH /clientes/atualizar-dados")
  class AtualizarDados {

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("Deveria retornar código 200 quando estiver autenticado como cliente")
    public void teste12() throws Exception {
      var json = """
              {
                "id": 1,
                "nome": "exemplo"
              }
              """;

      var detalhamentoClienteDto = criarDetalhamentoClienteDto();

      given(clienteService.atualizarDados(any(), any()))
              .willReturn(detalhamentoClienteDto);

      var response = mvc.perform(
              patch("/clientes/atualizar-dados")
                      .content(json)
                      .contentType(MediaType.APPLICATION_JSON)
      ).andReturn().getResponse();

      Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("Deveria retornar código 403 quando estiver autenticado como cliente mas não é o dono da conta")
    public void teste13() throws Exception {
      var json = """
              {
                "id": 1,
                "nome": "exemplo"
              }
              """;

      given(clienteService.atualizarDados(any(), any()))
              .willThrow(AccessDeniedException.class);

      var response = mvc.perform(
              patch("/clientes/atualizar-dados")
                      .content(json)
                      .contentType(MediaType.APPLICATION_JSON)
      ).andReturn().getResponse();

      Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("Deveria retornar código 400 quando estiver autenticado como cliente mas faltando o id no body")
    public void teste14() throws Exception {
      var detalhamentoClienteDto = criarDetalhamentoClienteDto();
      var json = """
              {"nome": "exemplo"}
              """;

      given(clienteService.atualizarDados(any(), any()))
              .willReturn(detalhamentoClienteDto);

      var response = mvc.perform(
              patch("/clientes/atualizar-dados")
                      .content(json)
                      .contentType(MediaType.APPLICATION_JSON)
      ).andReturn().getResponse();

      Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Deveria retornar código 403 quando estiver autenticado como admin")
    public void teste15() throws Exception {
      var response = mvc.perform(
              patch("/clientes/atualizar-dados")
      ).andReturn().getResponse();

      Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    @WithMockUser(roles = "BARBEIRO")
    @DisplayName("Deveria retornar código 403 quando estiver autenticado como barbeiro")
    public void teste16() throws Exception {
      var response = mvc.perform(
              patch("/clientes/atualizar-dados")
      ).andReturn().getResponse();

      Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    @DisplayName("Deveria retornar código 403 quando não estiver autenticado")
    public void teste17() throws Exception {
      var response = mvc.perform(
              patch("/clientes/atualizar-dados")
      ).andReturn().getResponse();

      Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("Deveria retornar código 404 quando o cliente não existir no banco de dados")
    public void teste18() throws Exception {
      var json = """
              {
                "id": 1,
                "nome": "exemplo"
              }
              """;

      given(clienteService.atualizarDados(any(), any()))
              .willThrow(NoSuchElementException.class);

      var response = mvc.perform(
              patch("/clientes/atualizar-dados")
                      .content(json)
                      .contentType(MediaType.APPLICATION_JSON)
      ).andReturn().getResponse();

      Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

  }

  @Nested
  @DisplayName("DELETE /clientes/{id}")
  class DeletarCliente {

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("Deveria retornar código 204 quando estiver autenticado como cliente")
    public void teste19() throws Exception {
      var response = mvc.perform(
              delete("/clientes/1")
      ).andReturn().getResponse();

      Assertions.assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Deveria retornar código 204 quando estiver autentucado como admin")
    public void teste20() throws Exception {
      var response = mvc.perform(
              delete("/clientes/1")
      ).andReturn().getResponse();

      Assertions.assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("Deveria retornar código 403 quando estiver autenticado como cliente mas não for dono da conta")
    public void teste21() throws Exception {
      doThrow(AccessDeniedException.class)
              .when(clienteService)
              .deletarCliente(any(), any());

      var response = mvc.perform(
              delete("/clientes/1")
      ).andReturn().getResponse();

      Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    @WithMockUser(roles = "BARBEIRO")
    @DisplayName("Deveria retornar código 403 quando estiver autenticado como barbeiro")
    public void teste22() throws Exception {
      var response = mvc.perform(
              delete("/clientes/1")
      ).andReturn().getResponse();

      Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    @DisplayName("Deveria retornar código 403 quando não estiver autenticado")
    public void teste23() throws Exception {
      var response = mvc.perform(
              delete("/clientes/1")
      ).andReturn().getResponse();

      Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("Deveria retornar código 404 quando o cliente não existir no banco de dados")
    public void teste24() throws Exception {
      doThrow(NoSuchElementException.class)
              .when(clienteService)
              .deletarCliente(any(), any());

      var response = mvc.perform(
              delete("/clientes/1")
      ).andReturn().getResponse();

      Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

  }

  @Nested
  @DisplayName("PATCH /clientes/{id}/reativar")
  class ReativarCliente {

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Deveria retornar código 200 quando estiver autenticado como admin")
    public void teste25() throws Exception {
      var detalhamentoClienteDto = criarDetalhamentoClienteDto();

      given(clienteService.reativarCliente(any())).willReturn(detalhamentoClienteDto);

      var response = mvc.perform(
              patch("/clientes/1/reativar")
      ).andReturn().getResponse();

      Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("Deveria retornar código 403 quando estiver autenticado como cliente")
    public void teste26() throws Exception {
      var response = mvc.perform(
              patch("/clientes/1/reativar")
      ).andReturn().getResponse();

      Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    @WithMockUser(roles = "BARBEIRO")
    @DisplayName("Deveria retornar código 403 quando estivar autenticado como barbeiro")
    public void teste27() throws Exception {
      var response = mvc.perform(
              patch("/clientes/1/reativar")
      ).andReturn().getResponse();

      Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    @DisplayName("Deveria retornar código 403 quando não estivar autenticado")
    public void teste28() throws Exception {
      var response = mvc.perform(
              patch("/clientes/1/reativar")
      ).andReturn().getResponse();

      Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Deveria retornar código 404 quando o cliente não cadastrado no banco")
    public void teste29() throws Exception {
      given(clienteService.reativarCliente(any()))
              .willThrow(NoSuchElementException.class);

      var response = mvc.perform(
              patch("/clientes/1/reativar")
      ).andReturn().getResponse();

      Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Deveria retornar código 400 quando o cliente já estiver ativo")
    public void teste30() throws Exception {
      given(clienteService.reativarCliente(any()))
              .willThrow(RegraDeNegocioException.class);

      var response = mvc.perform(
              patch("/clientes/1/reativar")
      ).andReturn().getResponse();

      Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

  }

}