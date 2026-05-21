package com.example.claro.controller;

import com.example.claro.model.Producto;
import com.example.claro.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    /** Listar todos los productos */
    @GetMapping
    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    /** Filtrar por tipo: CELULAR, COMPUTADOR, ACCESORIOS */
    @GetMapping("/tipo/{tipo}")
    public List<Producto> listarPorTipo(@PathVariable String tipo) {
        return productoRepository.findAll()
                .stream()
                .filter(p -> p.getTipo().toString().equalsIgnoreCase(tipo))
                .toList();
    }

    /** Obtener un producto por ID */
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtener(@PathVariable Long id) {
        return productoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
