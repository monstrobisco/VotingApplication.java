package com.sicredi.voting;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
		title = "Voting Backend ",
		version = "1.0",
		description = "sample documents"
))
public class VotingApplication {

	public static void main(String[] args) {
		SpringApplication.run(VotingApplication.class, args);
	}

}
