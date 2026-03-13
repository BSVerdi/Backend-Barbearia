package br.com.alvorada.barbearia.dto.barbeiro;

import java.time.LocalDateTime;

public record HorarioDto(
        LocalDateTime dataHora
) {
}
