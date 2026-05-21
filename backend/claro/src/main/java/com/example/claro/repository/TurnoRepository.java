package com.example.claro.repository;

import com.example.claro.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TurnoRepository extends JpaRepository<Turno, Long> {

    List<Turno> findByUsuarioIdOrderByFechaCreacionAsc(Long usuarioId);

    List<Turno> findByEstadoOrderByPrioridadActualAscFechaCreacionAsc(Turno.EstadoTurno estado);

    // Cola de prioritarios pendientes, orden FIFO
    List<Turno> findByEstadoAndEsPrioritarioOrderByFechaCreacionAsc(
            Turno.EstadoTurno estado, Boolean esPrioritario);

    // Contar turnos pendientes de un usuario en un departamento (evitar duplicados)
    long countByUsuarioIdAndDepartamentoIdAndEstado(
            Long usuarioId, Long departamentoId, Turno.EstadoTurno estado);

    // Último número correlativo del día para un prefijo dado
    @Query("SELECT t FROM Turno t WHERE t.departamento.codigoPrefijo = :prefijo ORDER BY t.id DESC LIMIT 1")
    Optional<Turno> findLastByPrefijo(String prefijo);
}
