package com.project.catchtable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.project")
@EntityScan(basePackages = "com.project.multimoduledatabase.entity")
@EnableJpaRepositories(basePackages = "com.project.multimoduledatabase.repository")
public class CatchTableApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatchTableApplication.class, args);
	}

}
