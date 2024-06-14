package com.stack.park;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class ParkApplication {

	public static void main(String[] args) {
		
		// Charger les variables d'environnement
		Dotenv dotenv = Dotenv.load();
		
		// Load variable d'environment
		String environment = dotenv.get("SPRING_PROFILES_ACTIVE", "dev"); // si "SPRING_PROFILES_ACTIVE" n'est pas défini, utilise "dev" comme valeur par défaut.
		System.setProperty("string.profiles.active", environment); // cela informe Spring Boot du profil à utilisé ("dev" ou "prod")

		// Load config DB basé par l'environment
		if ("prod".equalsIgnoreCase(environment)) {
            System.out.println("Loading production database configuration...");
            System.out.println("URL: " + dotenv.get("DATABASE_URL_PROD"));
            System.out.println("Username: " + dotenv.get("DATABASE_USERNAME_PROD"));
            System.setProperty("spring.datasource.url", dotenv.get("DATABASE_URL_PROD"));
            System.setProperty("spring.datasource.username", dotenv.get("DATABASE_USERNAME_PROD"));
            System.setProperty("spring.datasource.password", dotenv.get("DATABASE_PASSWORD_PROD"));
            System.setProperty("spring.datasource.driver-class-name", dotenv.get("DATABASE_DRIVER_CLASS_NAME_PROD"));
        } else {
            System.out.println("Loading development database configuration...");
            System.out.println("URL: " + dotenv.get("DATABASE_URL_DEV"));
            System.out.println("Username: " + dotenv.get("DATABASE_USERNAME_DEV"));
            System.setProperty("spring.datasource.url", dotenv.get("DATABASE_URL_DEV"));
            System.setProperty("spring.datasource.username", dotenv.get("DATABASE_USERNAME_DEV"));
            System.setProperty("spring.datasource.password", dotenv.get("DATABASE_PASSWORD_DEV"));
            System.setProperty("spring.datasource.driver-class-name", dotenv.get("DATABASE_DRIVER_CLASS_NAME_DEV"));
        }

		SpringApplication.run(ParkApplication.class, args);
	}

}
