package br.com.alvorada.barbearia.dto.usuario;

import br.com.alvorada.barbearia.model.Perfil;
import jakarta.validation.constraints.NotNull;

public record AtualizacaoUsuarioPerfilDto(@NotNull Long id, @NotNull Perfil perfil) {
}
