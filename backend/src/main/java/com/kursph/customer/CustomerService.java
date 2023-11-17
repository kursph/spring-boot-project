package com.kursph.customer;

import com.kursph.exception.DuplicateResourceException;
import com.kursph.exception.RequestValidationException;
import com.kursph.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    private final CustomerDAO customerDAO;
    private final PasswordEncoder passwordEncoder;
    private final CustomerDTOMapper customerDTOMapper;

    public CustomerService(@Qualifier("jpa") CustomerDAO customerDAO, PasswordEncoder passwordEncoder, CustomerDTOMapper customerDTOMapper) {
        this.customerDAO = customerDAO;
        this.passwordEncoder = passwordEncoder;
        this.customerDTOMapper = customerDTOMapper;
    }

    public List<CustomerDTO> getAllCustomers() {
        return customerDAO.selectAllCustomers()
                .stream()
                .map(customerDTOMapper)
                .collect(Collectors.toList());
    }

    public CustomerDTO getCustomer(Integer id) {
        return getCustomerDTO(id);
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        String email = customerRegistrationRequest.email();
        existsPersonWithEmail(email);

        customerDAO.insertCustomer(new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                passwordEncoder.encode(customerRegistrationRequest.password()),
                customerRegistrationRequest.age(),
                customerRegistrationRequest.gender()
        ));
    }

    public void deleteCustomer(Integer id) {
        Customer customer = getCustomerObject(id);

        customerDAO.removeCustomer(customer);
    }

    public void updateCustomer(Integer id, CustomerUpdateRequest customerUpdateRequest) {
        Customer customer = getCustomerObject(id);

        boolean changes = false;

        String name = customerUpdateRequest.name();
        String email = customerUpdateRequest.email();
        Integer age = customerUpdateRequest.age();

        if (name != null && !customer.getName().equals(name)) {
            customer.setName(name);
            changes = true;
        }

        if (email != null && !customer.getEmail().equals(email)) {
            existsPersonWithEmail(email);

            customer.setEmail(email);
            changes = true;
        }

        if (age != null && !Objects.equals(customer.getAge(), age)) {
            customer.setAge(age);
            changes = true;
        }

        if (!changes) {
            throw new RequestValidationException("no changes have been made");
        }

        customerDAO.updateCustomer(customer);
    }

    private void existsPersonWithEmail(String email) {
        if (customerDAO.existsPersonWithEmail(email)) {
            throw new DuplicateResourceException("email is already taken");
        }
    }

    private CustomerDTO getCustomerDTO(Integer id) {
        return customerDAO.selectCustomerById(id)
                .map(customerDTOMapper)
                .orElseThrow(
                () -> new ResourceNotFoundException("customer with id %s not found".formatted(id))
        );
    }

    private Customer getCustomerObject(Integer id) {
        return customerDAO.selectCustomerById(id)
                .orElseThrow(
                () -> new ResourceNotFoundException("customer with id %s not found".formatted(id))
        );
    }
}
