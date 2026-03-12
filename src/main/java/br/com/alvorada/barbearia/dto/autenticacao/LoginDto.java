package br.com.alvorada.barbearia.dto.autenticacao;

import jakarta.validation.constraints.NotBlank;

public record LoginDto(
        @NotBlank String email,
        @NotBlank String senha
) {
}
