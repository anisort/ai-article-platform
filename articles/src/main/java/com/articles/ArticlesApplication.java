package com.articles;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Defines articles application.
 */
@SpringBootApplication
@EnableCaching
public class ArticlesApplication {

/**
 * Performs main.
 * @param args the args
 */
	public static void main(String[] args) {
		SpringApplication.run(ArticlesApplication.class, args);
		System.out.println(java.util.TimeZone.getDefault().getID());

	}

}


