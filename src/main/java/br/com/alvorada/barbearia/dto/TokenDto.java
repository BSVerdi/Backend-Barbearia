package br.com.alvorada.barbearia.dto;

public record TokenDto(
        String tokenAcesso,
        String refreshToken
) {
}
