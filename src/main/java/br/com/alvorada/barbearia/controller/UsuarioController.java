package br.com.alvorada.barbearia.controller;

import br.com.alvorada.barbearia.dto.usuario.AtualizacaoUsuarioPerfilDto;
import br.com.alvorada.barbearia.dto.usuario.DetalhamentoUsuarioDto;
import br.com.alvorada.barbearia.security.service.TokenService;
import br.com.alvorada.barbearia.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

  @Autowired
  private UsuarioService service;

  @Autowired
  private TokenService tokenService;

  @GetMapping
  public ResponseEntity<List<DetalhamentoUsuarioDto>> listarUsuarios() {
    var usuarios = service.listarUsuarios();

    return ResponseEntity.ok(usuarios);
  }

  @GetMapping("/{id}")
  public ResponseEntity<DetalhamentoUsuarioDto> detalharUsuario(
          @PathVariable Long id
  ) {
    var usuario = service.detalharUsuario(id);

    return ResponseEntity.ok(usuario);
  }

  @PatchMapping("/perfil")
  public ResponseEntity<DetalhamentoUsuarioDto> atualizarPerfil(
          @Valid @RequestBody AtualizacaoUsuarioPerfilDto dto
  ) {
    var usuario = service.atualizarPerfil(dto);

    return ResponseEntity.ok(usuario);
  }

}
