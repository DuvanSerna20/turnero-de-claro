package com.example.claro.repository;

import com.example.claro.model.PlanServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlanServicioRepository extends JpaRepository<PlanServicio, Long> {
    List<PlanServicio> findByTipo(PlanServicio.TipoPlan tipo);
}
