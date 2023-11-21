package com.kursph.customer;

import com.kursph.exception.DuplicateResourceException;
import com.kursph.exception.ResourceNotFoundException;
import com.kursph.s3.S3Buckets;
import com.kursph.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    private CustomerService customerService;
    @Mock
    private CustomerDAO customerDAO;
    @Mock
    private PasswordEncoder passwordEncoder;
    private final CustomerDTOMapper customerDTOMapper = new CustomerDTOMapper();
    @Mock
    private S3Service s3Service;
    @Mock
    private S3Buckets s3Buckets;

    @BeforeEach
    void setUp() {
        customerService = new CustomerService(customerDAO, passwordEncoder, customerDTOMapper, s3Service, s3Buckets);
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
                "foobar", "rrr",
                "rrr@gmail.com",
                20,
                "MALE",
                "22222"
        );

        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerDTO expected = customerDTOMapper.apply(customer);

        CustomerDTO actual = customerService.getCustomer(id);
        assertThat(actual).isEqualTo(expected);
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
                "Alex", email, "foobar", 19, "MALE"
        );

        String hash = "657567hkfkjg(/&/$";
        when(passwordEncoder.encode("foobar")).thenReturn(hash);

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
        assertThat(capturedCustomer.getPassword()).isEqualTo(hash);
    }

    @Test
    void willThrowWhenEmailExistsWhileAddingACustomer() {
        String email = "alex@gmail.com";

        when(customerDAO.existsPersonWithEmail(email)).thenReturn(true);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Alex", email, "foobar", 19, "MALE"
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
                "foobar", "ggg",
                "ggg@gmail.com",
                20,
                "MALE",
                "22222"
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
                "foobar", "ggg",
                "ggg@gmail.com",
                20,
                "MALE",
                "22222"
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
    }

    @Test
    void canUploadProfileImage() {
        /*
        String email = "test@gmail.com";
        int id = 1;
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Alex", email, "foobar", 19, "MALE"
        );

        when(customerDAO.existsPersonWithEmail(email)).thenReturn(true);

        byte[] bytes = "Hello World".getBytes();

        MultipartFile multipartFile = new MockMultipartFile("file", bytes);

        String bucket = "customer-bucket";
        when(s3Buckets.getCustomer()).thenReturn(bucket);

        customerService.uploadCustomerProfileImage(id, multipartFile);

        ArgumentCaptor<String> profileImageIdArgumentCaptor = ArgumentCaptor.forClass(String.class);

        verify(customerDAO).updateCustomerProfileImageId(profileImageIdArgumentCaptor.capture(), eq(id));

        verify(s3Service).putObject(bucket, "profile-images/%s/%s".formatted(id, profileImageIdArgumentCaptor.getValue()), bytes);
         */
    }

    @Test
    void canDownloadProfileImage() {
        int customerId = 10;
        String profileImageId = "2222";
        Customer customer = new Customer(
                customerId,
                "Alex",
                "alex@gmail.com",
                "password",
                19,
                "MALE",
                profileImageId
        );
        when(customerDAO.selectCustomerById(customerId)).thenReturn(Optional.of(customer));

        String bucket = "customer-bucket";
        when(s3Buckets.getCustomer()).thenReturn(bucket);

        byte[] expectedImage = "image".getBytes();

        when(s3Service.getObject(
                bucket,
                "profile-images/%s/%s".formatted(customerId, profileImageId))
        ).thenReturn(expectedImage);

        byte[] actualImage = customerService.getCustomerProfileImage(customerId);

        assertThat(actualImage).isEqualTo(expectedImage);
    }

    @Test
    void cannotDownloadWhenNoProfileImageId() {
        /*
        int customerId = 10;
        String profileImageId = "2222";
        Customer customer = new Customer(
                customerId,
                "Alex",
                "alex@gmail.com",
                "password",
                19,
                "MALE",
                profileImageId
        );

        when(customerDAO.selectCustomerById(customerId)).thenReturn(Optional.of(customer));

        assertThatThrownBy(() -> customerService.getCustomerProfileImage(customerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] profile image not found".formatted(customerId));

        verifyNoInteractions(s3Buckets);
        verifyNoInteractions(s3Service);
        */
    }

    @Test
    void cannotDownloadProfileImageWhenCustomerDoesNotExists() {
        int customerId = 10;

        when(customerDAO.selectCustomerById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getCustomerProfileImage(customerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(customerId));

        verifyNoInteractions(s3Buckets);
        verifyNoInteractions(s3Service);
    }
}