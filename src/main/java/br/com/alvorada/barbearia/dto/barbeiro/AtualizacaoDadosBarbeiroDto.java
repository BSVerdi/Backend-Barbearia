package br.com.alvorada.barbearia.dto.barbeiro;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalTime;

public record AtualizacaoDadosBarbeiroDto(
        @NotNull Long id,
        String nome,
        @Pattern(regexp = "\\(?\\d{2}\\)?\\d?\\d{4}-?\\d{4}") String telefone,
        LocalTime horarioInicio,
        LocalTime horarioFim
) {
}
