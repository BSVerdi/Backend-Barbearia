package br.com.alvorada.barbearia.dto.autenticacao;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenDto(@NotBlank String refreshToken) {
}
