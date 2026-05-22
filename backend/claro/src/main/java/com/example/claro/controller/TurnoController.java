package com.example.claro.controller;

import com.example.claro.dto.TurnoRequestDTO;
import com.example.claro.model.Turno;
import com.example.claro.repository.TurnoRepository;
import com.example.claro.service.TurnoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador REST del módulo de turnos.
 * Recibe peticiones HTTP y delega la lógica compleja a TurnoService.
 */
@RestController
@RequestMapping("/api/turnos")
public class TurnoController {

    @Autowired
    private TurnoRepository turnoRepository;
    @Autowired
    private TurnoService turnoService;

    /**
     * GET /api/turnos/cola
     * Devuelve turnos PENDIENTES ordenados por prioridad (pantalla del turnero).
     */
    @GetMapping("/cola")
    public List<Turno> cola() {
        return turnoRepository.findByEstadoOrderByPrioridadActualAscFechaCreacionAsc(
                Turno.EstadoTurno.PENDIENTE);
    }

    /**
     * GET /api/turnos/usuario/{usuarioId}
     * Historial de turnos de un cliente.
     */
    @GetMapping("/usuario/{usuarioId}")
    public List<Turno> porUsuario(@PathVariable Long usuarioId) {
        return turnoRepository.findByUsuarioIdOrderByFechaCreacionAsc(usuarioId);
    }

    /**
     * POST /api/turnos
     * Crea un turno nuevo. Body: { usuarioId, departamentoId, esPrioritario }.
     */
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

    /**
     * PUT /api/turnos/siguiente/llamar
     * Llama al siguiente turno s y aging (TurnoService).
     */
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
