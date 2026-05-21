package com.example.claro.repository;

import com.example.claro.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TurnoRepository extends JpaRepository<Turno, Long> {
    List<Turno> findByUsuarioIdOrderByFechaCreacionAsc(Long usuarioId);
    List<Turno> findByEstadoOrderByPrioridadActualAscFechaCreacionAsc(Turno.EstadoTurno estado);
}
