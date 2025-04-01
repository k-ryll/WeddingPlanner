package com.example.wedding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WeddingPlannerApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(WeddingPlannerApplication.class, args);
	}

}
