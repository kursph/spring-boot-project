package com.kursph.customer;

import com.kursph.AbstractTestcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerJDBCDataAccessServiceTest extends AbstractTestcontainers {
    private CustomerJDBCDataAccessService customerJDBCDataAccessService;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();

    @BeforeEach
    void setUp() {
        customerJDBCDataAccessService = new CustomerJDBCDataAccessService(
                getJdbcTemplate(),
                customerRowMapper
        );
    }

    @Test
    void selectAllCustomers() {
        Customer customer = new Customer(
                "aaa",
                "aaa@gmail.com",
                11
        );

        customerJDBCDataAccessService.insertCustomer(customer);

        List<Customer> customers = customerJDBCDataAccessService.selectAllCustomers();

        assertThat(customers).isNotEmpty();
    }

    @Test
    void selectCustomerById() {
        String email = "bbbb@gmail.com";
        Customer customer = new Customer(
                "bbb",
                email,
                11
        );

        customerJDBCDataAccessService.insertCustomer(customer);

        int id = customerJDBCDataAccessService.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        Optional<Customer> actual = customerJDBCDataAccessService.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId().equals(id));
            assertThat(c.getEmail().equals(customer.getEmail()));
            assertThat(c.getName().equals(customer.getName()));
            assertThat(c.getAge().equals(customer.getAge()));
        });
    }

    @Test
    void willReturnEmptyWhenSelectCustomerById() {
        int id = -1;

        Optional<Customer> customer = customerJDBCDataAccessService.selectCustomerById(id);

        assertThat(customer).isEmpty();
    }

    @Test
    void insertCustomer() {
    }

    @Test
    void existsPersonWithEmail() {
        String email = "ccc@gmail.com";
        Customer customer = new Customer(
                "ccc",
                email,
                11
        );
        customerJDBCDataAccessService.insertCustomer(customer);

        boolean actual = customerJDBCDataAccessService.existsPersonWithEmail(email);
        assertThat(actual).isTrue();
    }

    @Test
    void removeCustomer() {
        String email = "bbbbb@gmail.com";
        Customer customer = new Customer(
                "bbb",
                email,
                11
        );
        customerJDBCDataAccessService.insertCustomer(customer);

        int id = customerJDBCDataAccessService.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        customerJDBCDataAccessService.removeCustomer(customer);

        Optional<Customer> actual = customerJDBCDataAccessService.selectCustomerById(id);
        assertThat(actual).isNotPresent();
    }

    @Test
    void updateCustomer() {
        String email = "ddd@gmail.com";
        Customer customer = new Customer(
                "ddd",
                email,
                20
        );

        customerJDBCDataAccessService.insertCustomer(customer);

        int id = customerJDBCDataAccessService.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        Customer update = new Customer();
        update.setId(id);
        update.setName("foo");
        update.setEmail(UUID.randomUUID().toString());
        update.setAge(22);

        customerJDBCDataAccessService.updateCustomer(update);

        Optional<Customer> actual = customerJDBCDataAccessService.selectCustomerById(id);

        assertThat(actual).isPresent().hasValue(update);
    }
}