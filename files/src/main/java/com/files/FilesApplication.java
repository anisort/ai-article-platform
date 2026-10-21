package com.files;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot entry point for the files microservice.
 */
/**
 * Defines files application.
 */
@SpringBootApplication
public class FilesApplication {

	/**
	 * Bootstraps and runs the files Spring Boot application.
	 *
	 * @param args command-line arguments passed to the application
	 */
/**
 * Performs main.
 * @param args the args
 */
	public static void main(String[] args) {
		SpringApplication.run(FilesApplication.class, args);
	}

}
