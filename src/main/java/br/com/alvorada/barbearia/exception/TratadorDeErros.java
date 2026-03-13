package br.com.alvorada.barbearia.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class TratadorDeErros {

  @ExceptionHandler({EntityNotFoundException.class, NoSuchElementException.class})
  public ResponseEntity<Void> tratarErro404() {
    return ResponseEntity.notFound().build();
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<List<ErroValidacaoDto>> tratarErro400(
          MethodArgumentNotValidException ex
  ) {
    var error = ex.getFieldErrors();

    return ResponseEntity.badRequest().body(
            error.stream().map(ErroValidacaoDto::new).toList()
    );
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<String> tratarErro403(AccessDeniedException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<String> tratarErro401(AuthenticationException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
  }

  @ExceptionHandler(RegraDeNegocioException.class)
  public ResponseEntity<String> tratarRegraDeNegocio(RegraDeNegocioException ex) {
    return ResponseEntity.badRequest().body(ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> tratarErro500(Exception ex) {
    return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Erro: " + ex.getLocalizedMessage());
  }

  private record ErroValidacaoDto(String campo, String mensagem) {

    public ErroValidacaoDto(FieldError erro) {
      this(erro.getField(), erro.getDefaultMessage());
    }

  }

}
