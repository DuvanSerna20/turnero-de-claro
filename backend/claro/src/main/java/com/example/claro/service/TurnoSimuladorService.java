package com.example.claro.service;

import com.example.claro.model.Turno;
import com.example.claro.repository.TurnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TurnoSimuladorService {

    @Autowired
    private TurnoRepository turnoRepository;

    @Autowired
    private EmailService emailService;

    // Se ejecuta cada 1 minuto para avanzar la cola.
    @Scheduled(fixedRate = 60000)
    public void simularAvanceTurnos() {
        System.out.println("⏳ [Simulador Turnos] Revisando cola de turnos...");

        // 1. Finalizar turnos que están siendo atendidos (LLAMADO)
        List<Turno> llamados = turnoRepository
                .findByEstadoOrderByPrioridadActualAscFechaCreacionAsc(Turno.EstadoTurno.LLAMADO);
        for (Turno t : llamados) {
            t.setEstado(Turno.EstadoTurno.ATENDIDO);
            t.setFechaAtencion(LocalDateTime.now());
            turnoRepository.save(t);
            System.out.println("✅ Turno " + t.getNumeroCorrelativo() + " ha sido atendido y finalizado.");
        }

        // 2. Llamar a los siguientes pendientes
        List<Turno> pendientes = turnoRepository
                .findByEstadoOrderByPrioridadActualAscFechaCreacionAsc(Turno.EstadoTurno.PENDIENTE);

        // Llamaremos a máximo 1 turno a la vez para simular 1 cajero desocupándose
        int cajerosDisponibles = 1;
        int llamadosAhora = 0;

        for (Turno p : pendientes) {
            if (llamadosAhora >= cajerosDisponibles)
                break;

            p.setEstado(Turno.EstadoTurno.LLAMADO);
            p.setFechaLlamado(LocalDateTime.now());
            turnoRepository.save(p);
            System.out.println("🔔 Turno " + p.getNumeroCorrelativo() + " ha sido llamado a ventanilla.");

            // 3. Enviar correo al usuario
            if (p.getUsuario() != null && p.getUsuario().getEmail() != null) {
                String asunto = "🔔 ¡Es tu turno en Claro!";
                String html = "<div style='font-family: Arial, sans-serif; text-align: center; color: #333; max-width: 500px; margin: 0 auto; border: 1px solid #ddd; padding: 20px; border-radius: 10px;'>"
                        + "<img src='https://i.postimg.cc/ZRBrKVd6/images.jpg' width='120' alt='Claro' style='margin-bottom: 20px;'>"
                        + "<h2 style='color: #da291c;'>¡Hola " + p.getUsuario().getNombres() + "!</h2>"
                        + "<p style='font-size: 16px;'>Es el momento, tu turno <strong style='font-size: 24px; color: #da291c; display: block; margin: 15px 0;'>"
                        + p.getNumeroCorrelativo() + "</strong> ha sido llamado a ventanilla.</p>"
                        + "<p style='font-size: 16px;'>Por favor, acércate para ser atendido por uno de nuestros asesores.</p>"
                        + "<hr style='border: none; border-top: 1px solid #eee; margin: 20px 0;'>"
                        + "<p style='font-size: 12px; color: #888;'>Gracias por preferir Claro. Este es un mensaje automático, por favor no respondas a este correo.</p>"
                        + "</div>";
                emailService.enviarCorreoHtml(p.getUsuario().getEmail(), asunto, html);
            }

            llamadosAhora++;
        }
    }
}
