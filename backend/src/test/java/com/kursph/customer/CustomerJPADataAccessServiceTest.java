package com.kursph.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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
        Page<Customer> page = mock(Page.class);
        List<Customer> customers = List.of(new Customer());
        when(page.getContent()).thenReturn(customers);
        when(customerRepository.findAll(any(Pageable.class))).thenReturn(page);
        // When
        List<Customer> expected = customerJPADataAccessService.selectAllCustomers();

        // Then
        assertThat(expected).isEqualTo(customers);
        ArgumentCaptor<Pageable> pageArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(customerRepository).findAll(pageArgumentCaptor.capture());
        assertThat(pageArgumentCaptor.getValue()).isEqualTo(Pageable.ofSize(100));
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
                "foobar", 20,
                "MALE"
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
                "foobar", 20,
                "MALE"
        );
        customerJPADataAccessService.removeCustomer(customer);

        verify(customerRepository).delete(customer);
    }

    @Test
    void updateCustomer() {
        Customer customer = new Customer(
                "llll",
                "llll@gmail.com",
                "foobar", 20,
                "MALE"
        );
        customerJPADataAccessService.updateCustomer(customer);

        verify(customerRepository).save(customer);
    }

    @Test
    void canUpdateProfileImageId() {
        String profileImageId = "2222";
        Integer customerId = 1;

        customerJPADataAccessService.updateCustomerProfileImageId(profileImageId, customerId);

        verify(customerRepository).updateProfileImageId(profileImageId, customerId);
    }
}