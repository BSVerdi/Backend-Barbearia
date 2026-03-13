package br.com.alvorada.barbearia.security.service;

import br.com.alvorada.barbearia.exception.RegraDeNegocioException;
import br.com.alvorada.barbearia.model.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

  @Value("${secret.key}")
  private String secret;

  public String gerarToken(Usuario usuario) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);

      return JWT.create()
              .withIssuer("Alvorada Barbearia")
              .withSubject(usuario.getUsername())
              .withExpiresAt(expiracao(30))
              .sign(algorithm);
    } catch (JWTCreationException ex) {
      throw new RegraDeNegocioException("Erro ao gerar token JWT de acesso!");
    }
  }

  public String gerarRefreshToken(Usuario usuario) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);

      return JWT.create()
              .withIssuer("Alvorada Barbearia")
              .withSubject(usuario.getId().toString())
              .withExpiresAt(expiracao(120))
              .sign(algorithm);
    } catch (JWTCreationException ex) {
      throw new RegraDeNegocioException("Erro ao gerar token JWT de acesso!");
    }
  }

  public String verificarToken(String token) {
    DecodedJWT decodedJWT;

    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);

      JWTVerifier verifier = JWT.require(algorithm)
              .withIssuer("Alvorada Barbearia")
              .build();

      decodedJWT = verifier.verify(token);

      return decodedJWT.getSubject();
    } catch (JWTVerificationException ex) {
      throw new RegraDeNegocioException("Erro ao verificar token JWT de acesso!");
    }
  }

  private Instant expiracao(Integer minutos) {
    return LocalDateTime.now().plusMinutes(minutos).toInstant(ZoneOffset.of("-03:00"));
  }

}
