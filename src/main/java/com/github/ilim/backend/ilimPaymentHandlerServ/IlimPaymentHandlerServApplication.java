package com.github.ilim.backend.ilimPaymentHandlerServ;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class IlimPaymentHandlerServApplication {

	public static void main(String[] args) {
		SpringApplication.run(IlimPaymentHandlerServApplication.class, args);
	}

}
