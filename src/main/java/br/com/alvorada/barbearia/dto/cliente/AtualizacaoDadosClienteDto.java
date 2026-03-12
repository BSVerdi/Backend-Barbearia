package br.com.alvorada.barbearia.dto.cliente;

import jakarta.validation.constraints.NotNull;

public record AtualizacaoDadosClienteDto(
        @NotNull Long id,
        String nome,
        String telefone
) {
}
