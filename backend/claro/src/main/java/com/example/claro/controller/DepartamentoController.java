package com.example.claro.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import com.example.claro.model.Departamento;
import com.example.claro.repository.DepartamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departamentos")
@Tag(name = "Departamentos", description = "Áreas de atención del turnero (Ventas, Soporte, etc.)")
public class DepartamentoController {

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @GetMapping
    public List<Departamento> listar() {
        return departamentoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Departamento> obtener(@PathVariable Long id) {
        return departamentoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
