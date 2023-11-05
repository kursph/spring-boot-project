package com.kursph;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@SpringBootApplication
public class SpringBootExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootExampleApplication.class, args);
	}

	@GetMapping("/")
	public GreetResponse greet(@RequestParam(name = "name", required = false) String name) {
		String greetResponseName = name == null || name.isBlank() ? "Hello" : name;

		return new GreetResponse(
				greetResponseName,
				List.of("Java", "PHP"),
				new Person("Alex", 28, 30_000)
		);
	}

	record Person(String name, int age, double savings) {}

	record GreetResponse(
			String response,
			List<String> favProgrammingLanguages,
			Person person
	) {
	}
}
