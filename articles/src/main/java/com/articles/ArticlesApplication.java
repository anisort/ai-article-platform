package com.articles;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ArticlesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArticlesApplication.class, args);
		System.out.println(java.util.TimeZone.getDefault().getID());

	}

}


