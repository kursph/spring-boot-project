package com.kursph.customer;

import com.kursph.exception.DuplicateResourceException;
import com.kursph.exception.RequestValidationException;
import com.kursph.exception.ResourceNotFoundException;
import com.kursph.s3.S3Buckets;
import com.kursph.s3.S3Service;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    private final CustomerDAO customerDAO;
    private final PasswordEncoder passwordEncoder;
    private final CustomerDTOMapper customerDTOMapper;
    private final S3Service s3Service;
    private final S3Buckets s3Buckets;

    public CustomerService(@Qualifier("jpa") CustomerDAO customerDAO,
                           PasswordEncoder passwordEncoder,
                           CustomerDTOMapper customerDTOMapper,
                           S3Service s3Service,
                           S3Buckets s3Buckets
    ) {
        this.customerDAO = customerDAO;
        this.passwordEncoder = passwordEncoder;
        this.customerDTOMapper = customerDTOMapper;
        this.s3Service = s3Service;
        this.s3Buckets = s3Buckets;
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

    public void uploadCustomerProfileImage(Integer customerId, MultipartFile file) {
        getCustomerObject(customerId);
        String profileImageId = UUID.randomUUID().toString();

        try {
            s3Service.putObject(
                    s3Buckets.getCustomer(),
                    "profile-images/%s/%s".formatted(customerId, profileImageId),
                    file.getBytes()
            );
        } catch (IOException e) {
            throw new RuntimeException("failed to upload profile image", e);
        }

        customerDAO.updateCustomerProfileImageId(profileImageId, customerId);
    }

    public byte[] getCustomerProfileImage(Integer customerId) {
        CustomerDTO customer = customerDAO.selectCustomerById(customerId)
                .map(customerDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "customer with id [%s] not found".formatted(customerId)
                ));

        if (StringUtils.isBlank(customer.profileImageId())) {
            throw new ResourceNotFoundException(
                    "customer with id [%s] profile image not found".formatted(customerId));
        }

        byte[] profileImage = s3Service.getObject(
                s3Buckets.getCustomer(),
                "profile-images/%s/%s".formatted(customerId, customer.profileImageId())
        );

        return profileImage;
    }
}
