package com.example.claro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Punto de entrada de la aplicación Spring Boot.
 * Al ejecutar el main(), se levanta el servidor en el puerto 8080.
 */
@SpringBootApplication
@EnableScheduling // Permite tareas automáticas (ej. TurnoSimuladorService cada 60 seg)
public class ClaroApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClaroApplication.class, args);
	}

}
