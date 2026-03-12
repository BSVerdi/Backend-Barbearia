package br.com.alvorada.barbearia.dto.autenticacao;

public record TokenDto(
        String tokenAcesso,
        String refreshToken
) {
}
