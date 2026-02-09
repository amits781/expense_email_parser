package com.aidy.expense;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableRetry
@EnableScheduling
public class BankParserApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankParserApplication.class, args);
	}

}