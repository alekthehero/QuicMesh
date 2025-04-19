package com.nebula.socketv2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Socketv2Application {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Socketv2Application.class);
		app.setWebApplicationType(WebApplicationType.NONE);
		app.run(args);
	}
}
