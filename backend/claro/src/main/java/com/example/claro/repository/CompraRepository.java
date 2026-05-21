package com.example.claro.repository;

import com.example.claro.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CompraRepository extends JpaRepository<Compra, Long> {
    List<Compra> findByUsuarioIdOrderByFechaCompraDesc(Long usuarioId);
}
