package br.com.alvorada.barbearia.dto.agendamento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AgendarDto(
        @NotBlank String dataHora,
        @NotNull Long clienteId,
        @NotNull Long barbeiroId
) {
}
