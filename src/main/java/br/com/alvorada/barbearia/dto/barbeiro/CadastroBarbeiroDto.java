package br.com.alvorada.barbearia.dto.barbeiro;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CadastroBarbeiroDto(
        @NotBlank String nome,
        @NotBlank @Email String email,

        @NotBlank
        @Pattern(regexp = "\\(?\\d{2}\\)?\\d?\\d{4}-?\\d{4}")
        String telefone,

        @NotBlank String horarioInicio,
        @NotBlank String horarioFim
) {
}
