package com.kursph.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDAO {
    List<Customer> selectAllCustomers();
    Optional<Customer> selectCustomerById(Integer id);
    void insertCustomer(Customer customer);
    boolean existsPersonWithEmail(String email);
    void removeCustomer(Customer customer);
    void updateCustomer(Customer customer);
    Optional<Customer> selectUserByEmail(String email);
    void updateCustomerProfileImageId(String profileImageId, Integer customerId);
}
