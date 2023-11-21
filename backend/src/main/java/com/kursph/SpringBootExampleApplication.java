package com.kursph;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.kursph.customer.Customer;
import com.kursph.customer.CustomerRepository;
import com.kursph.s3.S3Buckets;
import com.kursph.s3.S3Service;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Random;

@SpringBootApplication
public class SpringBootExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootExampleApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(
			PasswordEncoder passwordEncoder,
			CustomerRepository customerRepository,
			S3Service s3Service,
			S3Buckets s3Buckets
	) {
		return args -> {
			createRandomCustomer(customerRepository, passwordEncoder);
			testBucketUploadAndDownload(s3Service, s3Buckets);
		};
	}

	private static void testBucketUploadAndDownload(S3Service s3Service,
													S3Buckets s3Buckets) {
		s3Service.putObject(
				s3Buckets.getCustomer(),
				"foo/bar/jamila",
				"Hello World".getBytes()
		);

		byte[] obj = s3Service.getObject(
				s3Buckets.getCustomer(),
				"foo/bar/jamila"
		);

		System.out.println("Hooray: " + new String(obj));
	}

	private static void createRandomCustomer(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
		var faker = new Faker();
		Random random = new Random();
		Name name = faker.name();
		String firstName = name.firstName();
		String lastName = name.lastName();
		int age = random.nextInt(16, 99);
		String gender = age % 2 == 0 ? "MALE" : "FEMALE";
		String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@amigoscode.com";
		Customer customer = new Customer(
				firstName +  " " + lastName,
				email,
				passwordEncoder.encode("password"),
				age,
				gender);
		customerRepository.save(customer);
		System.out.println(email);
	}
}
