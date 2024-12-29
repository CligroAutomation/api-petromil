package com.example.demo;

import jakarta.annotation.PostConstruct;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import java.util.logging.Logger;

@SpringBootApplication
public class BackendForPetromilGasStationApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendForPetromilGasStationApplication.class, args);

		System.out.println("Zona horaria actual (TimeZone): " + TimeZone.getDefault().getID());
		System.out.println("Zona horaria actual (ZoneId): " + ZoneId.systemDefault().getId());

		ZonedDateTime now = ZonedDateTime.now();
		System.out.println("Hora actual: " + now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z")));


	}



}
