package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

@SpringBootApplication
public class BackendForPetromilGasStationApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(BackendForPetromilGasStationApplication.class, args);

		System.out.println("Zona horaria actual (TimeZone): " + TimeZone.getDefault().getID());
		System.out.println("Zona horaria actual (ZoneId): " + ZoneId.systemDefault().getId());

		ZonedDateTime now = ZonedDateTime.now();
		System.out.println("Hora actual: " + now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z")));

		PasswordEncoder passwordEncoder = context.getBean(PasswordEncoder.class);
		System.out.println(passwordEncoder.encode("cligro1234"));
	}
}
