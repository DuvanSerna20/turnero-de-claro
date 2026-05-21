package com.example.claro.service;

import com.example.claro.dto.TurnoRequestDTO;
import com.example.claro.model.Departamento;
import com.example.claro.model.Turno;
import com.example.claro.model.Usuario;
import com.example.claro.repository.DepartamentoRepository;
import com.example.claro.repository.TurnoRepository;
import com.example.claro.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TurnoService {

    // ── Contador del ratio 3:1 ─────────────────────────────────────────────────
    // Cuántos prioritarios seguidos se han llamado. Cuando llega a 3,
    // el siguiente llamado es forzado a ser un regular (si hay alguno).
    private final AtomicInteger consecutivosPrioritarios = new AtomicInteger(0);

    // Minutos de espera antes de que el aging "suba" la prioridad de un regular
    private static final int MINUTOS_AGING = 15;

    @Autowired private TurnoRepository turnoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private DepartamentoRepository departamentoRepository;

    // ── 1. Crear turno ─────────────────────────────────────────────────────────
    public Turno crearTurno(TurnoRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Departamento dept = departamentoRepository.findById(dto.getDepartamentoId())
                .orElseThrow(() -> new IllegalArgumentException("Departamento no encontrado"));

        // Evitar que un usuario tenga dos turnos pendientes en el mismo departamento
        long pendientes = turnoRepository.countByUsuarioIdAndDepartamentoIdAndEstado(
                dto.getUsuarioId(), dto.getDepartamentoId(), Turno.EstadoTurno.PENDIENTE);
        if (pendientes > 0) {
            throw new IllegalStateException("Ya tienes un turno pendiente en este departamento");
        }

        // Número correlativo: prefijo + número secuencial (ej. P-001, V-002)
        String numero = generarNumeroCorrelativo(dept.getCodigoPrefijo());

        Turno turno = new Turno();
        turno.setUsuario(usuario);
        turno.setDepartamento(dept);
        turno.setNumeroCorrelativo(numero);
        turno.setEsPrioritario(dto.getEsPrioritario());

        // La prioridad base viene del departamento; si es prioritario se reduce
        // para que aparezca antes en la cola (menor número = mayor prioridad)
        BigDecimal prioridad = BigDecimal.valueOf(dept.getNivelPrioridad());
        if (Boolean.TRUE.equals(dto.getEsPrioritario())) {
            prioridad = prioridad.subtract(BigDecimal.valueOf(5));
        }
        turno.setPrioridadActual(prioridad);

        return turnoRepository.save(turno);
    }

    // ── 2. Seleccionar el siguiente turno a llamar (ratio 3:1 + aging) ─────────
    public Optional<Turno> seleccionarSiguiente() {
        List<Turno> prioritarios = turnoRepository
                .findByEstadoAndEsPrioritarioOrderByFechaCreacionAsc(
                        Turno.EstadoTurno.PENDIENTE, true);
        List<Turno> regulares = turnoRepository
                .findByEstadoAndEsPrioritarioOrderByFechaCreacionAsc(
                        Turno.EstadoTurno.PENDIENTE, false);

        // Aging: si el regular más antiguo lleva más de MINUTOS_AGING, forzar su llamado
        if (!regulares.isEmpty()) {
            Turno masAntiguoRegular = regulares.get(0);
            long minutosEsperando = java.time.Duration.between(
                    masAntiguoRegular.getFechaCreacion(), LocalDateTime.now()).toMinutes();
            if (minutosEsperando >= MINUTOS_AGING) {
                consecutivosPrioritarios.set(0); // resetear contador
                return Optional.of(masAntiguoRegular);
            }
        }

        // Ratio 3:1: si ya llamamos 3 prioritarios seguidos, forzar uno regular
        if (consecutivosPrioritarios.get() >= 3 && !regulares.isEmpty()) {
            consecutivosPrioritarios.set(0);
            return Optional.of(regulares.get(0));
        }

        // Normal: prioridad → FIFO
        if (!prioritarios.isEmpty()) {
            consecutivosPrioritarios.incrementAndGet();
            return Optional.of(prioritarios.get(0));
        }

        if (!regulares.isEmpty()) {
            consecutivosPrioritarios.set(0);
            return Optional.of(regulares.get(0));
        }

        return Optional.empty(); // No hay nadie esperando
    }

    // ── 3. Número correlativo ──────────────────────────────────────────────────
    private String generarNumeroCorrelativo(String prefijo) {
        Optional<Turno> ultimo = turnoRepository.findLastByPrefijo(prefijo);
        int siguiente = 1;
        if (ultimo.isPresent()) {
            String numeroActual = ultimo.get().getNumeroCorrelativo();
            // Extraer la parte numérica (ej. "V-007" → "007" → 7)
            try {
                String parteNumerica = numeroActual.replaceAll("[^0-9]", "");
                siguiente = Integer.parseInt(parteNumerica) + 1;
            } catch (NumberFormatException ignored) {
                siguiente = (int) (turnoRepository.count() + 1);
            }
        }
        return prefijo + "-" + String.format("%03d", siguiente);
    }
}
