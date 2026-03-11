package br.com.alvorada.barbearia.controller;

import br.com.alvorada.barbearia.dto.LoginDto;
import br.com.alvorada.barbearia.dto.RefreshTokenDto;
import br.com.alvorada.barbearia.dto.TokenDto;
import br.com.alvorada.barbearia.model.Usuario;
import br.com.alvorada.barbearia.repository.UsuarioRepository;
import br.com.alvorada.barbearia.security.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AutenticacaoController {

  private final AuthenticationManager authenticationManager;
  private final TokenService tokenService;
  private final UsuarioRepository usuarioRepository;

  public AutenticacaoController(
          AuthenticationManager authenticationManager,
          TokenService tokenService,
          UsuarioRepository usuarioRepository
  ) {
    this.authenticationManager = authenticationManager;
    this.tokenService = tokenService;
    this.usuarioRepository = usuarioRepository;
  }

  @PostMapping("/login")
  public ResponseEntity<TokenDto> efetuarLogin(@Valid @RequestBody LoginDto dto) {

    var tokenAutenticacao = new UsernamePasswordAuthenticationToken(
            dto.email(),
            dto.senha()
    );
    var autenticacao = authenticationManager.authenticate(tokenAutenticacao);

    var usuario = (Usuario) autenticacao.getPrincipal();

    var tokenAcesso = tokenService.gerarToken(usuario);
    var refreshToken = tokenService.gerarRefreshToken(usuario);

    return ResponseEntity.ok(new TokenDto(tokenAcesso, refreshToken));
  }

  @PostMapping("/atualizar-token")
  public ResponseEntity<TokenDto> atualizarToken(
          @Valid @RequestBody RefreshTokenDto dto
  ) {

    var refreshToken = dto.refreshToken();
    var idUsuario = Long.valueOf(tokenService.verificarToken(refreshToken));
    var usuario = usuarioRepository.findById(idUsuario).orElseThrow();

    var tokenAcesso = tokenService.gerarToken(usuario);
    var tokenAtualizacao = tokenService.gerarRefreshToken(usuario);

    return ResponseEntity.ok(new TokenDto(tokenAcesso, tokenAtualizacao));
  }

}
