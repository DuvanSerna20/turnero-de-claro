package com.example.claro.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import com.example.claro.model.Compra;
import com.example.claro.model.PlanServicio;
import com.example.claro.model.Usuario;
import com.example.claro.repository.CompraRepository;
import com.example.claro.repository.PlanServicioRepository;
import com.example.claro.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.claro.repository.ProductoRepository;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/api/compras")
@Tag(name = "Compras", description = "Historial y registro de compras de planes")
public class CompraController {

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PlanServicioRepository planServicioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    /** Historial de compras de un usuario */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> porUsuario(@PathVariable Long usuarioId) {
        List<Compra> compras = compraRepository.findByUsuarioIdOrderByFechaCompraDesc(usuarioId);
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Compra c : compras) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("fechaCompra", c.getFechaCompra());
            map.put("metodoPago", c.getMetodoPago());
            
            // Reconstruir el objeto para que el frontend de Angular no se rompa
            if (c.getTipoItem() == Compra.TipoItem.PLAN) {
                planServicioRepository.findById(c.getItemId()).ifPresent(plan -> {
                    map.put("plan", plan);
                });
            } else if (c.getTipoItem() == Compra.TipoItem.PRODUCTO) {
                productoRepository.findById(c.getItemId()).ifPresent(prod -> {
                    // Mapeamos el producto con las mismas llaves que un plan para la UI
                    Map<String, Object> mockPlan = new HashMap<>();
                    mockPlan.put("nombre", prod.getNombre());
                    mockPlan.put("tipo", prod.getTipo().toString());
                    mockPlan.put("descripcion", prod.getDescripcion());
                    mockPlan.put("precio", prod.getPrecio());
                    map.put("plan", mockPlan); 
                });
            }
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    /** Registrar una nueva compra */
    @PostMapping
    public ResponseEntity<?> comprar(@RequestBody Map<String, Object> body) {
        Long usuarioId = Long.valueOf(body.get("usuarioId").toString());
        Long planId = Long.valueOf(body.get("planId").toString());

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        Optional<PlanServicio> planOpt = planServicioRepository.findById(planId);

        if (usuarioOpt.isEmpty()) return ResponseEntity.badRequest().body("Usuario no encontrado");
        if (planOpt.isEmpty()) return ResponseEntity.badRequest().body("Plan no encontrado");

        PlanServicio plan = planOpt.get();

        Compra compra = new Compra();
        compra.setUsuario(usuarioOpt.get());
        compra.setTipoItem(Compra.TipoItem.PLAN);
        compra.setItemId(plan.getId());
        compra.setPrecioPagado(plan.getPrecio());

        if (body.containsKey("metodoPago")) {
            compra.setMetodoPago(body.get("metodoPago").toString());
        }

        return ResponseEntity.ok(compraRepository.save(compra));
    }
}
