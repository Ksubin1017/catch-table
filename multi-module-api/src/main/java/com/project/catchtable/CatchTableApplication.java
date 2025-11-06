package com.project.catchtable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.project.multimoduledatabase.entity")
public class CatchTableApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatchTableApplication.class, args);
	}

}
