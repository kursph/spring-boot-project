package com.kursph.customer;

import com.kursph.AbstractTestcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest extends AbstractTestcontainers {
    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
    }

    @Test
    void existsCustomerByEmail() {
        Customer customer = new Customer(
                "vvv",
                "vvv@gmail.com",
                "foobar", 20,
                "MALE"
        );
        customerRepository.save(customer);

        boolean actual = customerRepository.existsCustomerByEmail(customer.getEmail());
        assertThat(actual).isTrue();
    }
}