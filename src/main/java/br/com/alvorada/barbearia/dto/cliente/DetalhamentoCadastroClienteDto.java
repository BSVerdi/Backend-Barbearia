package br.com.alvorada.barbearia.dto.cliente;

public record DetalhamentoCadastroClienteDto(
        DetalhamentoClienteDto cliente,
        String tokenAcesso,
        String refreshToken
) {
}
