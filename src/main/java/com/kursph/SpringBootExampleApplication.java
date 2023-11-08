package com.kursph;

import com.kursph.customer.Customer;
import com.kursph.customer.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;


@SpringBootApplication
public class SpringBootExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootExampleApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(CustomerRepository customerRepository) {
		return args -> {
			/* Customer customer1 = new Customer(
					"Alex",
					"alex111@gmail.com",
					22
			);

			Customer customer2 = new Customer(
					"Alex",
					"alex11@gmail.com",
					22
			);

			List<Customer> customers = List.of(customer1, customer2);
			customerRepository.saveAll(customers); */
		};
	}
}
