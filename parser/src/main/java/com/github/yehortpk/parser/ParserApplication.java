package com.github.yehortpk.parser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.microsoft.playwright.Playwright;

@SpringBootApplication
public class ParserApplication {

	public static void main(String[] args) {
		Playwright.create().close();
		SpringApplication.run(ParserApplication.class, args);
	}

}
