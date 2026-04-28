package com.project4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement(proxyTargetClass = true)
public class Project4Application {

	public static void main(String[] args) {
		SpringApplication.run(Project4Application.class, args);
	}


}
