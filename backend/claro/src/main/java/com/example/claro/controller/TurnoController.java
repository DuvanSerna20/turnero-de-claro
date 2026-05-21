package com.example.claro.controller;

import com.example.claro.dto.TurnoRequestDTO;
import com.example.claro.model.Turno;
import com.example.claro.repository.TurnoRepository;
import com.example.claro.service.TurnoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/turnos")
@Tag(name = "Turnos", description = "Cola de turnos, creación y gestión (ratio 3:1 y aging)")
public class TurnoController {

    @Autowired
    private TurnoRepository turnoRepository;
    @Autowired
    private TurnoService turnoService;

    /** Cola de turnos PENDIENTES ordenados por prioridad y FIFO */
    @GetMapping("/cola")
    public List<Turno> cola() {
        return turnoRepository.findByEstadoOrderByPrioridadActualAscFechaCreacionAsc(
                Turno.EstadoTurno.PENDIENTE);
    }

    /** Historial de turnos de un usuario */
    @GetMapping("/usuario/{usuarioId}")
    public List<Turno> porUsuario(@PathVariable Long usuarioId) {
        return turnoRepository.findByUsuarioIdOrderByFechaCreacionAsc(usuarioId);
    }

    /** Crear un nuevo turno */
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody TurnoRequestDTO dto) {
        try {
            Turno guardado = turnoService.crearTurno(dto);
            return ResponseEntity.ok(guardado);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /** Seleccionar y llamar el siguiente turno respetando ratio 3:1 y aging */
    @PutMapping("/siguiente/llamar")
    public ResponseEntity<?> llamarSiguiente() {
        Optional<Turno> siguiente = turnoService.seleccionarSiguiente();
        if (siguiente.isEmpty()) {
            return ResponseEntity.ok("No hay turnos pendientes en la cola");
        }
        Turno t = siguiente.get();
        t.setEstado(Turno.EstadoTurno.LLAMADO);
        t.setFechaLlamado(java.time.LocalDateTime.now());
        return ResponseEntity.ok(turnoRepository.save(t));
    }

    /** Llamar un turno específico por ID */
    @PutMapping("/{id}/llamar")
    public ResponseEntity<?> llamar(@PathVariable Long id) {
        return turnoRepository.findById(id).map(t -> {
            t.setEstado(Turno.EstadoTurno.LLAMADO);
            t.setFechaLlamado(java.time.LocalDateTime.now());
            return ResponseEntity.ok(turnoRepository.save(t));
        }).orElse(ResponseEntity.notFound().build());
    }

    /** Marcar turno como ATENDIDO */
    @PutMapping("/{id}/atender")
    public ResponseEntity<?> atender(@PathVariable Long id) {
        return turnoRepository.findById(id).map(t -> {
            t.setEstado(Turno.EstadoTurno.ATENDIDO);
            t.setFechaAtencion(java.time.LocalDateTime.now());
            return ResponseEntity.ok(turnoRepository.save(t));
        }).orElse(ResponseEntity.notFound().build());
    }

    /** Cancelar turno */
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(@PathVariable Long id) {
        return turnoRepository.findById(id).map(t -> {
            t.setEstado(Turno.EstadoTurno.CANCELADO);
            return ResponseEntity.ok(turnoRepository.save(t));
        }).orElse(ResponseEntity.notFound().build());
    }
}
