package com.example.claro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ClaroApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClaroApplication.class, args);
	}

}
