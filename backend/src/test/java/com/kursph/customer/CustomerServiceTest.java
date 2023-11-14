package com.kursph.customer;

import com.kursph.exception.DuplicateResourceException;
import com.kursph.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    private CustomerService customerService;
    @Mock
    private CustomerDAO customerDAO;

    @BeforeEach
    void setUp() {
        customerService = new CustomerService(customerDAO);
    }

    @Test
    void getAllCustomers() {
        customerService.getAllCustomers();

        verify(customerDAO).selectAllCustomers();
    }

    @Test
    void getCustomer() {
        int id = 1;
        Customer customer = new Customer(
                id,
                "rrr",
                "rrr@gmail.com",
                20,
                "MALE"
        );

        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        Customer actual = customerService.getCustomer(id);
        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void willThrowWhenGetCustomerReturnsEmptyOptional() {
        int id = 1;

        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id %s not found".formatted(id));
    }

    @Test
    void addCustomer() {
        String email = "alex@gmail.com";

        when(customerDAO.existsPersonWithEmail(email)).thenReturn(false);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Alex", email, 19, "MALE"
        );

        customerService.addCustomer(request);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        );

        verify(customerDAO).insertCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }

    @Test
    void willThrowWhenEmailExistsWhileAddingACustomer() {
        String email = "alex@gmail.com";

        when(customerDAO.existsPersonWithEmail(email)).thenReturn(true);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Alex", email, 19, "MALE"
        );

        assertThatThrownBy(() -> customerService.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email is already taken");

        verify(customerDAO, never()).insertCustomer(any());
    }

    @Test
    void deleteCustomer() {
        int id = 1;
        Customer customer = new Customer(
                id,
                "ggg",
                "ggg@gmail.com",
                20,
                "MALE"
        );

        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        customerService.deleteCustomer(id);

        verify(customerDAO).removeCustomer(customer);
    }

    @Test
    void willThrowDeleteCustomerByIdNotExists() {
        int id = 1;
        Customer customer = new Customer(
                id,
                "ggg",
                "ggg@gmail.com",
                20,
                "MALE"
        );

        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.deleteCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id %s not found".formatted(id));

        verify(customerDAO, never()).removeCustomer(any());
    }

    @Test
    void updateCustomer() {
        // todo implement tests after checking repo
        // GIVEN

        // WHEN

        // THEN
    }
}