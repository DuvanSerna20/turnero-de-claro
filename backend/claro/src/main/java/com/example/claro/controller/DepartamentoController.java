package com.example.claro.controller;

import com.example.claro.model.Departamento;
import com.example.claro.repository.DepartamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST de departamentos.
 * El turnero de Angular llama GET /api/departamentos para mostrar las tarjetas de áreas.
 */
@RestController
@RequestMapping("/api/departamentos")
public class DepartamentoController {

    @Autowired
    private DepartamentoRepository departamentoRepository;

    /** GET /api/departamentos — Lista todos los departamentos */
    @GetMapping
    public List<Departamento> listar() {
        return departamentoRepository.findAll();
    }

    /** GET /api/departamentos/{id} — Obtiene un departamento por ID */
    @GetMapping("/{id}")
    public ResponseEntity<Departamento> obtener(@PathVariable Long id) {
        return departamentoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
