package com.krishiYatra.krishiYatra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class KrishiYatraApplication {

	public static void main(String[] args) {
		SpringApplication.run(KrishiYatraApplication.class, args);
	}

}
