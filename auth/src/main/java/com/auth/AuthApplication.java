package com.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the authentication Spring Boot application.
 */
/**
 * Defines auth application.
 */
@SpringBootApplication
public class AuthApplication {

	/**
	 * Bootstraps and runs the authentication application.
	 *
	 * @param args command-line arguments passed to the application
	 */
/**
 * Performs main.
 * @param args the args
 */
	public static void main(String[] args) {
		SpringApplication.run(AuthApplication.class, args);
	}

}
