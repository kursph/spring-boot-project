package com.kursph.customer;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("list")
public class CustomerDataAccessService implements CustomerDAO{
    private static List<Customer> customers;

    static {
        customers = new ArrayList<>();

        Customer customer1 = new Customer(
                1,
                "Alex",
                "alex@gmail.com",
                22,
                "MALE"
        );
        customers.add(customer1);
    }

    @Override
    public List<Customer> selectAllCustomers() {
        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        return customers.stream().filter(customer -> customer.getId().equals(id))
                .findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        customers.add(customer);
    }

    @Override
    public boolean existsPersonWithEmail(String email) {
        return false;
    }

    @Override
    public void removeCustomer(Customer customer) {

    }

    @Override
    public void updateCustomer(Customer customer) {

    }
}
