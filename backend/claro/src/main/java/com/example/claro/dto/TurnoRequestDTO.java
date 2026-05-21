package com.example.claro.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Datos para solicitar un nuevo turno")
public class TurnoRequestDTO {

    @NotNull(message = "El ID del departamento es obligatorio")
    @Schema(description = "ID del departamento de atención", example = "1")
    private Long departamentoId;

    @NotNull(message = "El ID del usuario es obligatorio")
    @Schema(description = "ID del usuario autenticado", example = "1")
    private Long usuarioId;

    @NotNull(message = "Debe indicar si el turno es prioritario o no")
    @Schema(description = "true = atención preferencial", example = "false")
    private Boolean esPrioritario;
}
