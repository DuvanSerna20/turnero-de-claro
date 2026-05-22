package com.example.claro.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TurnoRequestDTO {

    @NotNull(message = "El ID del departamento es obligatorio")
    private Long departamentoId;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "Debe indicar si el turno es prioritario o no")
    private Boolean esPrioritario;
}
