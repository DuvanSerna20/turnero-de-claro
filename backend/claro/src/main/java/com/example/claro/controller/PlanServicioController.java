package com.example.claro.controller;

import com.example.claro.model.PlanServicio;
import com.example.claro.repository.PlanServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/planes")
public class PlanServicioController {

    @Autowired
    private PlanServicioRepository planServicioRepository;

    @GetMapping
    public List<PlanServicio> listar() {
        return planServicioRepository.findAll();
    }

    @GetMapping("/tipo/{tipo}")
    public List<PlanServicio> listarPorTipo(@PathVariable String tipo) {
        return planServicioRepository.findByTipo(PlanServicio.TipoPlan.valueOf(tipo.toUpperCase()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanServicio> obtener(@PathVariable Long id) {
        return planServicioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
