package br.com.alvorada.barbearia.controller;

import br.com.alvorada.barbearia.dto.CadastroUsuarioDto;
import br.com.alvorada.barbearia.dto.DetalhamentoCadastroUsuarioDto;
import br.com.alvorada.barbearia.dto.DetalhamentoUsuarioDto;
import br.com.alvorada.barbearia.security.service.TokenService;
import br.com.alvorada.barbearia.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
public class UsuarioController {

  private final UsuarioService service;
  private final TokenService tokenService;

  public UsuarioController(UsuarioService service, TokenService tokenService) {
    this.service = service;
    this.tokenService = tokenService;
  }

  @PostMapping("/registrar")
  public ResponseEntity<DetalhamentoCadastroUsuarioDto> cadastrar(
          @Valid @RequestBody CadastroUsuarioDto dto,
          UriComponentsBuilder uriBuilder
  ) {

    var usuario = service.cadastrar(dto);
    var uri = uriBuilder
            .path("usuarios/{id}")
            .buildAndExpand(usuario.getId())
            .toUri();

    var tokenAcesso = tokenService.gerarToken(usuario);
    var refreshToken = tokenService.gerarRefreshToken(usuario);

    return ResponseEntity
            .created(uri)
            .body(new DetalhamentoCadastroUsuarioDto(
                    usuario,
                    tokenAcesso,
                    refreshToken
            ));
  }

  @GetMapping("/usuarios")
  public ResponseEntity<List<DetalhamentoUsuarioDto>> listarUsuarios() {

    var usuarios = service.listarUsuarios();

    return ResponseEntity.ok(usuarios);
  }

  @GetMapping("/usuarios/{id}")
  public ResponseEntity<DetalhamentoUsuarioDto> detalharUsuario(
          @PathVariable Long id
  ) {

    var usuario = service.detalharUsuario(id);

    return ResponseEntity.ok(usuario);
  }
}
