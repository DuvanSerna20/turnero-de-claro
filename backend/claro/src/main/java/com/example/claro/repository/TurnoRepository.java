package com.example.claro.repository;

import com.example.claro.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para turnos.
 * Contiene las consultas usadas por el turnero y TurnoService.
 */
public interface TurnoRepository extends JpaRepository<Turno, Long> {

    /** Historial de turnos de un usuario, del más antiguo al más reciente */
    List<Turno> findByUsuarioIdOrderByFechaCreacionAsc(Long usuarioId);

    /** Cola visible en pantalla: pendientes ordenados por prioridad y hora */
    List<Turno> findByEstadoOrderByPrioridadActualAscFechaCreacionAsc(Turno.EstadoTurno estado);

    /** Lista de prioritarios o regulares en cola (para ratio 3:1) */
    List<Turno> findByEstadoAndEsPrioritarioOrderByFechaCreacionAsc(
            Turno.EstadoTurno estado, Boolean esPrioritario);

    /** Evita que un usuario tenga dos turnos PENDIENTE en el mismo departamento */
    long countByUsuarioIdAndDepartamentoIdAndEstado(
            Long usuarioId, Long departamentoId, Turno.EstadoTurno estado);

    /** Obtiene el último turno de un prefijo para generar el siguiente número (VEN-003) */
    @Query("SELECT t FROM Turno t WHERE t.departamento.codigoPrefijo = :prefijo ORDER BY t.id DESC LIMIT 1")
    Optional<Turno> findLastByPrefijo(String prefijo);
}
