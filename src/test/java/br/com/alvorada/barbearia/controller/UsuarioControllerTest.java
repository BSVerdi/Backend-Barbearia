package br.com.alvorada.barbearia.controller;

import br.com.alvorada.barbearia.model.Usuario;
import br.com.alvorada.barbearia.service.UsuarioService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

@SpringBootTest
@AutoConfigureMockMvc
class UsuarioControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockitoBean
  private UsuarioService usuarioService;

  @MockitoBean
  private Usuario usuario;

  @Nested
  @WithMockUser(roles = "ADMIN")
  @DisplayName("Quando o usuario está autenticado")
  public class usuarioAutenticado {

    @Test
    @DisplayName("Deveria retornar codigo 200 para listar usuarios")
    public void teste01() throws Exception {
      var response = mvc.perform(
              get("/usuarios").contentType(MediaType.APPLICATION_JSON)
      ).andReturn().getResponse();

      Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    @DisplayName("Deveria retornar codigo 200 para detalhar usuario")
    public void teste02() throws Exception {
      var response = mvc.perform(
              get("/usuarios/1").contentType(MediaType.APPLICATION_JSON)
      ).andReturn().getResponse();

      Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    @DisplayName("Deveria retornar codigo 200 para atualizar perfil do usuario")
    public void teste03() throws Exception {
      var json = """
              {
                "id": 1,
                "perfil": "BARBEIRO"
              }
              """;

      var response = mvc.perform(
              patch("/usuarios/perfil")
                      .content(json)
                      .contentType(MediaType.APPLICATION_JSON)
      ).andReturn().getResponse();

      Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    @DisplayName("Deveria retornar codigo 400 para atualizar perfil do usuario")
    public void teste04() throws Exception {
      var json = "{}";

      var response = mvc.perform(
              patch("/usuarios/perfil")
                      .content(json)
                      .contentType(MediaType.APPLICATION_JSON)
      ).andReturn().getResponse();

      Assertions.assertEquals(400, response.getStatus());
    }

  }

  @Nested
  @DisplayName("Quando o usuario não está autenticado")
  public class usuarioNaoAutenticado {

    @Test
    @DisplayName("Deveria retornar codigo 403 para listar usuarios")
    public void teste01() throws Exception {
      var response = mvc.perform(
              get("/usuarios").contentType(MediaType.APPLICATION_JSON)
      ).andReturn().getResponse();

      Assertions.assertEquals(403, response.getStatus());
    }

    @Test
    @DisplayName("Deveria retornar codigo 403 para detalhar usuario")
    public void teste02() throws Exception {
      var response = mvc.perform(
              get("/usuarios/1").contentType(MediaType.APPLICATION_JSON)
      ).andReturn().getResponse();

      Assertions.assertEquals(403, response.getStatus());
    }

    @Test
    @DisplayName("Deveria retornar codigo 403 para atualizar perfil do usuario")
    public void teste03() throws Exception {
      var json = """
              {
                "id": 1,
                "perfil": "BARBEIRO"
              }
              """;

      var response = mvc.perform(
              patch("/usuarios")
                      .content(json)
                      .contentType(MediaType.APPLICATION_JSON)
      ).andReturn().getResponse();

      Assertions.assertEquals(403, response.getStatus());
    }

  }

}