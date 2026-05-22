package com.example.claro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

/**
 * Servicio de envío de correos electrónicos.
 * Usa la configuración SMTP de Gmail en application.properties.
 * Lo llama TurnoSimuladorService cuando se llama un turno a ventanilla.
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Envía un correo de texto plano.
     */
    public void enviarCorreo(String destinatario, String asunto, String mensaje) {
        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(destinatario);
            email.setSubject(asunto);
            email.setText(mensaje);

            mailSender.send(email);
            System.out.println("📧 Correo enviado exitosamente a: " + destinatario);
        } catch (Exception e) {
            System.err.println("❌ Error enviando correo a " + destinatario + ": " + e.getMessage());
        }
    }

    /**
     * Envía un correo con contenido HTML (logo, número de turno, etc.).
     */
    public void enviarCorreoHtml(String destinatario, String asunto, String contenidoHtml) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(contenidoHtml, true); // true = el cuerpo es HTML

            mailSender.send(message);
            System.out.println("📧 Correo HTML enviado exitosamente a: " + destinatario);
        } catch (Exception e) {
            System.err.println("❌ Error enviando correo HTML a " + destinatario + ": " + e.getMessage());
        }
    }
}
