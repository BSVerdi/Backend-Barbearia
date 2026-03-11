package br.com.alvorada.barbearia.security.filter;

import br.com.alvorada.barbearia.repository.UsuarioRepository;
import br.com.alvorada.barbearia.security.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class FiltroTokenAcesso extends OncePerRequestFilter {

  private final TokenService service;
  private final UsuarioRepository repository;

  public FiltroTokenAcesso(TokenService service, UsuarioRepository repository) {
    this.service = service;
    this.repository = repository;
  }

  @Override
  protected void doFilterInternal(
          HttpServletRequest request,
          HttpServletResponse response,
          FilterChain filterChain
  ) throws ServletException, IOException {
    String token = recuperarTokenRequisicao(request);

    if (token != null) {
      var email = service.verificarToken(token);
      var usuario = repository.findByEmailIgnoreCase(email).orElseThrow();

      var authentication = new UsernamePasswordAuthenticationToken(
              usuario,
              null,
              usuario.getAuthorities()
      );

      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    filterChain.doFilter(request, response);
  }

  private String recuperarTokenRequisicao(HttpServletRequest request) {
    var authorizationHeader = request.getHeader("Authorization");

    if (authorizationHeader != null) {
      return authorizationHeader.replace("Bearer ", "");
    }

    return null;
  }

}
