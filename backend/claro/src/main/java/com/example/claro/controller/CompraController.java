package com.example.claro.controller;

import com.example.claro.model.Compra;
import com.example.claro.model.PlanServicio;
import com.example.claro.model.Usuario;
import com.example.claro.repository.CompraRepository;
import com.example.claro.repository.PlanServicioRepository;
import com.example.claro.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador REST de compras de planes.
 * Registra compras y muestra el historial al usuario.
 */
@RestController
@RequestMapping("/api/compras")
public class CompraController {

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PlanServicioRepository planServicioRepository;

    /**
     * GET /api/compras/usuario/{usuarioId}
     * Historial de compras de planes del usuario.
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> porUsuario(@PathVariable Long usuarioId) {
        List<Compra> compras = compraRepository.findByUsuarioIdOrderByFechaCompraDesc(usuarioId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Compra c : compras) {
            if (c.getTipoItem() != Compra.TipoItem.PLAN) {
                continue;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("fechaCompra", c.getFechaCompra());
            map.put("metodoPago", c.getMetodoPago());

            planServicioRepository.findById(c.getItemId()).ifPresent(plan -> map.put("plan", plan));
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    /**
     * POST /api/compras
     * Registra compra de un plan. Body: { usuarioId, planId, metodoPago }.
     */
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
