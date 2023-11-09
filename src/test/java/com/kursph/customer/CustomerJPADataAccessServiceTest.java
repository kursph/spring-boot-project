package com.kursph.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

class CustomerJPADataAccessServiceTest {
    private CustomerJPADataAccessService customerJPADataAccessService;
    private AutoCloseable autoCloseable;
    @Mock
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        customerJPADataAccessService = new CustomerJPADataAccessService(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void selectAllCustomers() {
        customerJPADataAccessService.selectAllCustomers();

        verify(customerRepository).findAll();
    }

    @Test
    void selectCustomerById() {
        int id = 1;
        customerJPADataAccessService.selectCustomerById(id);

        verify(customerRepository).findById(id);
    }

    @Test
    void insertCustomer() {
        Customer customer = new Customer(
                "lll",
                "lll@gmail.com",
                20
        );
        customerJPADataAccessService.insertCustomer(customer);

        verify(customerRepository).save(customer);
    }

    @Test
    void existsPersonWithEmail() {
        String email = "test@gmail.com";
        customerJPADataAccessService.existsPersonWithEmail(email);

        verify(customerRepository).existsCustomerByEmail(email);
    }

    @Test
    void removeCustomer() {
        Customer customer = new Customer(
                "qqq",
                "qqq@gmail.com",
                20
        );
        customerJPADataAccessService.removeCustomer(customer);

        verify(customerRepository).delete(customer);
    }

    @Test
    void updateCustomer() {
        Customer customer = new Customer(
                "llll",
                "llll@gmail.com",
                20
        );
        customerJPADataAccessService.updateCustomer(customer);

        verify(customerRepository).save(customer);
    }
}