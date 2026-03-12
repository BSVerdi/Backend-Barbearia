package br.com.alvorada.barbearia.controller;

import br.com.alvorada.barbearia.dto.cliente.*;
import br.com.alvorada.barbearia.security.service.TokenService;
import br.com.alvorada.barbearia.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

  private final ClienteService service;
  private final TokenService tokenService;

  public ClienteController(ClienteService service, TokenService tokenService) {
    this.service = service;
    this.tokenService = tokenService;
  }

  @PostMapping("/cadastrar")
  public ResponseEntity<DetalhamentoCadastroClienteDto> cadastrar(
          @Valid @RequestBody CadastroClienteDto dto,
          UriComponentsBuilder uriBuilder
  ) {

    var cliente = service.cadastrar(dto);
    var uri = uriBuilder
            .path("clientes/{id}")
            .buildAndExpand(cliente.cliente().id())
            .toUri();

    return ResponseEntity.created(uri).body(cliente);
  }

  @GetMapping
  public ResponseEntity<List<ClienteDto>> listarClientes() {
    var clientes = service.listarClientes();

    return ResponseEntity.ok(clientes);
  }

  @PatchMapping("/atualizar-dados")
  public ResponseEntity<DetalhamentoClienteDto> atualizarDados(
          @Valid @RequestBody AtualizacaoDadosClienteDto dto
  ) {
    var cliente = service.atualizarDados(dto);

    return ResponseEntity.ok(cliente);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
    service.deletarCliente(id);

    return ResponseEntity.noContent().build();
  }

}
