package com.example.claro.service;

import com.example.claro.model.Turno;
import com.example.claro.repository.TurnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Simulador automático de la cola de turnos.
 * Se ejecuta cada 60 segundos sin intervención del usuario (requiere @EnableScheduling en ClaroApplication).
 */
@Service
public class TurnoSimuladorService {

    @Autowired private TurnoRepository turnoRepository;
    @Autowired private EmailService emailService;
    @Autowired private TurnoService turnoService;

    /**
     * Tarea programada: cada 60.000 ms (1 minuto).
     * 1) Pasa turnos LLAMADO → ATENDIDO
     * 2) Llama al siguiente PENDIENTE (ratio 3:1 + aging)
     * 3) Envía correo al cliente
     */
    @Scheduled(fixedRate = 60000)
    public void simularAvanceTurnos() {
        System.out.println("⏳ [Simulador] Revisando cola de turnos...");

        // Paso 1: finalizar turnos que ya estaban en ventanilla (LLAMADO)
        List<Turno> llamados = turnoRepository
                .findByEstadoOrderByPrioridadActualAscFechaCreacionAsc(Turno.EstadoTurno.LLAMADO);
        for (Turno t : llamados) {
            t.setEstado(Turno.EstadoTurno.ATENDIDO);
            t.setFechaAtencion(LocalDateTime.now());
            turnoRepository.save(t);
            System.out.println("✅ Turno " + t.getNumeroCorrelativo() + " finalizado → ATENDIDO");
        }

        // Paso 2: seleccionar y llamar el siguiente turno pendiente
        Optional<Turno> siguiente = turnoService.seleccionarSiguiente();
        if (siguiente.isEmpty()) {
            System.out.println("📭 No hay turnos pendientes en la cola.");
            return;
        }

        Turno turno = siguiente.get();
        turno.setEstado(Turno.EstadoTurno.LLAMADO);
        turno.setFechaLlamado(LocalDateTime.now());
        turnoRepository.save(turno);
        System.out.println("🔔 Turno " + turno.getNumeroCorrelativo()
                + (Boolean.TRUE.equals(turno.getEsPrioritario()) ? " [PRIORITARIO]" : " [REGULAR]")
                + " llamado a ventanilla.");

        // Paso 3: notificar por correo si el usuario tiene email
        if (turno.getUsuario() != null && turno.getUsuario().getEmail() != null) {
            String asunto = "🔔 ¡Es tu turno en Claro!";
            String tipoBadge = Boolean.TRUE.equals(turno.getEsPrioritario())
                    ? "<span style='background:#da291c;color:white;padding:4px 10px;border-radius:20px;font-size:13px;'>⭐ Atención Preferencial</span>"
                    : "";
            String html = "<div style='font-family:Arial,sans-serif;text-align:center;color:#333;"
                    + "max-width:500px;margin:0 auto;border:1px solid #ddd;padding:20px;border-radius:10px;'>"
                    + "<img src='https://i.postimg.cc/ZRBrKVd6/images.jpg' width='120' alt='Claro' style='margin-bottom:20px;'>"
                    + "<h2 style='color:#da291c;'>¡Hola " + turno.getUsuario().getNombres() + "!</h2>"
                    + tipoBadge
                    + "<p style='font-size:16px;margin-top:15px;'>Tu turno</p>"
                    + "<div style='font-size:36px;font-weight:bold;color:#da291c;margin:10px 0;'>"
                    + turno.getNumeroCorrelativo() + "</div>"
                    + "<p style='font-size:16px;'>ha sido llamado a ventanilla.</p>"
                    + "<p style='font-size:15px;'>Por favor acércate para ser atendido.</p>"
                    + "<hr style='border:none;border-top:1px solid #eee;margin:20px 0;'>"
                    + "<p style='font-size:12px;color:#888;'>Mensaje automático — Claro Colombia</p>"
                    + "</div>";
            emailService.enviarCorreoHtml(turno.getUsuario().getEmail(), asunto, html);
        }
    }
}
