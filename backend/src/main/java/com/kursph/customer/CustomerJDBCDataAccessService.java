package com.kursph.customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerJDBCDataAccessService implements CustomerDAO {
    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;

    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> selectAllCustomers() {
        String sql = """
                SELECT id, password, name, email, age, gender FROM customer
                """;

        return jdbcTemplate.query(sql, customerRowMapper);
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        String sql = """
                SELECT id, password, name, email, age, gender FROM customer WHERE id = ?
                """;

        return jdbcTemplate.query(sql, customerRowMapper, id).stream().findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        String sql = """
                INSERT INTO customer(name, email, password, age, gender) VALUES (?, ?, ?, ?, ?)
                """;
        int result = jdbcTemplate.update(sql, customer.getName(), customer.getEmail(), customer.getPassword(), customer.getAge(), customer.getGender());

        System.out.printf("%d row affected", result);
    }

    @Override
    public boolean existsPersonWithEmail(String email) {
        String sql = """
                SELECT count(id) FROM customer WHERE email = ?
                """;
        Integer result = jdbcTemplate.queryForObject(sql, Integer.class, email);

        return result != null && result > 0;
    }

    @Override
    public void removeCustomer(Customer customer) {
        String sql = """
                DELETE FROM customer WHERE id = ?
                """;
        int result = jdbcTemplate.update(sql, customer.getId());

        System.out.printf("%d row affected", result);
    }

    @Override
    public void updateCustomer(Customer customer) {
        if (customer.getName() != null) {
            String sql = "UPDATE customer SET name = ? WHERE id = ?";
            int result = jdbcTemplate.update(
                    sql,
                    customer.getName(),
                    customer.getId()
            );

            System.out.println("update customer name result = " + result);
        }
        if (customer.getAge() != null) {
            String sql = "UPDATE customer SET age = ? WHERE id = ?";
            int result = jdbcTemplate.update(
                    sql,
                    customer.getAge(),
                    customer.getId()
            );

            System.out.println("update customer age result = " + result);
        }
        if (customer.getEmail() != null) {
            String sql = "UPDATE customer SET email = ? WHERE id = ?";
            int result = jdbcTemplate.update(
                    sql,
                    customer.getEmail(),
                    customer.getId());

            System.out.println("update customer email result = " + result);
        }
        if (customer.getGender() != null) {
            String sql = "UPDATE customer SET gender = ? WHERE id = ?";
            int result = jdbcTemplate.update(
                    sql,
                    customer.getGender(),
                    customer.getId());

            System.out.println("update customer email result = " + result);
        }
    }
}
