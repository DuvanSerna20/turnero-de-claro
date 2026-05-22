package com.example.claro.repository;

import com.example.claro.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositorio JPA para compras realizadas por los usuarios.
 */
public interface CompraRepository extends JpaRepository<Compra, Long> {

    /** Historial de compras de un usuario, más recientes primero */
    List<Compra> findByUsuarioIdOrderByFechaCompraDesc(Long usuarioId);
}
