package com.example.claro.controller;

import com.example.claro.model.PlanServicio;
import com.example.claro.repository.PlanServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST de planes de servicio.
 * La tienda Angular consume GET /api/planes para mostrar el catálogo.
 */
@RestController
@RequestMapping("/api/planes")
public class PlanServicioController {

    @Autowired
    private PlanServicioRepository planServicioRepository;

    /** GET /api/planes — Todos los planes */
    @GetMapping
    public List<PlanServicio> listar() {
        return planServicioRepository.findAll();
    }

    /** GET /api/planes/tipo/{tipo} — Filtra por MOVIL, HOGAR o ENTRETENIMIENTO */
    @GetMapping("/tipo/{tipo}")
    public List<PlanServicio> listarPorTipo(@PathVariable String tipo) {
        return planServicioRepository.findByTipo(PlanServicio.TipoPlan.valueOf(tipo.toUpperCase()));
    }

    /** GET /api/planes/{id} — Un plan por ID */
    @GetMapping("/{id}")
    public ResponseEntity<PlanServicio> obtener(@PathVariable Long id) {
        return planServicioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
