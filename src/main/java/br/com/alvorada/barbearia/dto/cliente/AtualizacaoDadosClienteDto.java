package br.com.alvorada.barbearia.dto.cliente;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AtualizacaoDadosClienteDto(
        @NotNull Long id,
        String nome,
        @Pattern(regexp = "\\(?\\d{2}\\)?\\d?\\d{4}-?\\d{4}") String telefone
) {
}
