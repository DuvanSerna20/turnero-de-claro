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

/**
 * Servicio con la lógica de negocio del turnero.
 * Aquí están las reglas: crear turno, ratio 3:1, aging y número correlativo.
 */
@Service
public class TurnoService {

    /**
     * Contador en memoria: cuántos turnos prioritarios se llamaron seguidos.
     * Al llegar a 3, el siguiente debe ser un turno regular (ratio 3:1).
     */
    private final AtomicInteger consecutivosPrioritarios = new AtomicInteger(0);

    /** Minutos de espera para que un turno regular "suba" de prioridad (aging) */
    private static final int MINUTOS_AGING = 15;

    @Autowired private TurnoRepository turnoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private DepartamentoRepository departamentoRepository;

    /**
     * Crea un turno nuevo en estado PENDIENTE.
     * Valida usuario/departamento, evita duplicados y asigna número (ej. VEN-001).
     */
    public Turno crearTurno(TurnoRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Departamento dept = departamentoRepository.findById(dto.getDepartamentoId())
                .orElseThrow(() -> new IllegalArgumentException("Departamento no encontrado"));

        // Un usuario no puede tener dos turnos pendientes en el mismo departamento
        long pendientes = turnoRepository.countByUsuarioIdAndDepartamentoIdAndEstado(
                dto.getUsuarioId(), dto.getDepartamentoId(), Turno.EstadoTurno.PENDIENTE);
        if (pendientes > 0) {
            throw new IllegalStateException("Ya tienes un turno pendiente en este departamento");
        }

        String numero = generarNumeroCorrelativo(dept.getCodigoPrefijo());

        Turno turno = new Turno();
        turno.setUsuario(usuario);
        turno.setDepartamento(dept);
        turno.setNumeroCorrelativo(numero);
        turno.setEsPrioritario(dto.getEsPrioritario());

        // Prioridad base del departamento; si es preferencial se resta 5 (pasa antes)
        BigDecimal prioridad = BigDecimal.valueOf(dept.getNivelPrioridad());
        if (Boolean.TRUE.equals(dto.getEsPrioritario())) {
            prioridad = prioridad.subtract(BigDecimal.valueOf(5));
        }
        turno.setPrioridadActual(prioridad);

        return turnoRepository.save(turno);
    }

    /**
     * Decide cuál es el siguiente turno a llamar aplicando:
     * 1) Aging: regular con más de 15 min de espera va primero.
     * 2) Ratio 3:1: tras 3 prioritarios seguidos, toca un regular.
     * 3) Por defecto: primero prioritarios en orden FIFO, luego regulares.
     */
    public Optional<Turno> seleccionarSiguiente() {
        List<Turno> prioritarios = turnoRepository
                .findByEstadoAndEsPrioritarioOrderByFechaCreacionAsc(
                        Turno.EstadoTurno.PENDIENTE, true);
        List<Turno> regulares = turnoRepository
                .findByEstadoAndEsPrioritarioOrderByFechaCreacionAsc(
                        Turno.EstadoTurno.PENDIENTE, false);

        // Regla aging
        if (!regulares.isEmpty()) {
            Turno masAntiguoRegular = regulares.get(0);
            long minutosEsperando = java.time.Duration.between(
                    masAntiguoRegular.getFechaCreacion(), LocalDateTime.now()).toMinutes();
            if (minutosEsperando >= MINUTOS_AGING) {
                consecutivosPrioritarios.set(0);
                return Optional.of(masAntiguoRegular);
            }
        }

        // Regla ratio 3:1
        if (consecutivosPrioritarios.get() >= 3 && !regulares.isEmpty()) {
            consecutivosPrioritarios.set(0);
            return Optional.of(regulares.get(0));
        }

        // Flujo normal: prioritarios primero
        if (!prioritarios.isEmpty()) {
            consecutivosPrioritarios.incrementAndGet();
            return Optional.of(prioritarios.get(0));
        }

        if (!regulares.isEmpty()) {
            consecutivosPrioritarios.set(0);
            return Optional.of(regulares.get(0));
        }

        return Optional.empty();
    }

    /**
     * Genera el siguiente número de turno con prefijo del departamento (VEN-001, RET-002).
     */
    private String generarNumeroCorrelativo(String prefijo) {
        Optional<Turno> ultimo = turnoRepository.findLastByPrefijo(prefijo);
        int siguiente = 1;
        if (ultimo.isPresent()) {
            String numeroActual = ultimo.get().getNumeroCorrelativo();
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
