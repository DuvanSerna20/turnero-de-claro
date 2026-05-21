package com.example.claro.controller;

import com.example.claro.model.Departamento;
import com.example.claro.model.Turno;
import com.example.claro.model.Usuario;
import com.example.claro.repository.DepartamentoRepository;
import com.example.claro.repository.TurnoRepository;
import com.example.claro.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/turnos")
public class TurnoController {

    @Autowired
    private TurnoRepository turnoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    /** Cola de turnos PENDIENTES ordenados por prioridad */
    @GetMapping("/cola")
    public List<Turno> cola() {
        return turnoRepository.findByEstadoOrderByPrioridadActualAscFechaCreacionAsc(Turno.EstadoTurno.PENDIENTE);
    }

    /** Historial de turnos de un usuario */
    @GetMapping("/usuario/{usuarioId}")
    public List<Turno> porUsuario(@PathVariable Long usuarioId) {
        return turnoRepository.findByUsuarioIdOrderByFechaCreacionAsc(usuarioId);
    }

    /** Crear un nuevo turno */
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Map<String, Long> body) {
        Long usuarioId = body.get("usuarioId");
        Long departamentoId = body.get("departamentoId");

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        Optional<Departamento> deptOpt = departamentoRepository.findById(departamentoId);

        if (usuarioOpt.isEmpty()) return ResponseEntity.badRequest().body("Usuario no encontrado");
        if (deptOpt.isEmpty()) return ResponseEntity.badRequest().body("Departamento no encontrado");

        Departamento dept = deptOpt.get();
        Turno turno = new Turno();
        turno.setUsuario(usuarioOpt.get());
        turno.setDepartamento(dept);
        turno.setPrioridadActual(BigDecimal.valueOf(dept.getNivelPrioridad()));

        // Generar número correlativo: prefijo + cantidad de turnos + 1
        long count = turnoRepository.count() + 1;
        turno.setNumeroCorrelativo(dept.getCodigoPrefijo() + String.format("%03d", count));

        Turno guardado = turnoRepository.save(turno);
        return ResponseEntity.ok(guardado);
    }

    /** Llamar el siguiente turno PENDIENTE de un departamento */
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
