package br.com.alvorada.barbearia.dto.cliente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CadastroClienteDto(
        @NotBlank String nome,

        @NotBlank @Email String email,

        @NotBlank String senha,

        @NotBlank @Pattern(regexp = "\\(?\\d{2}\\)?\\d?\\d{4}-?\\d{4}") String telefone
) {
}
