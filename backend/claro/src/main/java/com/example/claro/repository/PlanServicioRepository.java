package com.example.claro.repository;

import com.example.claro.model.PlanServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositorio JPA para planes de servicio (tienda).
 */
public interface PlanServicioRepository extends JpaRepository<PlanServicio, Long> {

    /** Filtra planes por tipo: MOVIL, HOGAR o ENTRETENIMIENTO */
    List<PlanServicio> findByTipo(PlanServicio.TipoPlan tipo);
}
