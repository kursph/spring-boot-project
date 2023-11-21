package com.kursph.customer;

import com.kursph.jwt.JWTUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {
    private final CustomerService customerService;
    private final JWTUtil jwtUtil;

    public CustomerController(CustomerService customerService, JWTUtil jwtUtil) {
        this.customerService = customerService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public List<CustomerDTO> getCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("{id}")
    public CustomerDTO getCustomer(@PathVariable("id") Integer id) {
        return customerService.getCustomer(id);
    }

    @PostMapping
    public ResponseEntity<?> registerCustomer(@RequestBody CustomerRegistrationRequest customerRegistrationRequest) {
        customerService.addCustomer(customerRegistrationRequest);
        String jwt = jwtUtil.issueToken(customerRegistrationRequest.email(), "ROLE_USER");

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .build();
    }

    @DeleteMapping("{id}")
    public void deleteCustomer(@PathVariable("id") Integer id) {
        customerService.deleteCustomer(id);
    }

    @PutMapping("{id}")
    public void updateCustomer(@PathVariable("id") Integer id, @RequestBody CustomerUpdateRequest customerUpdateRequest) {
        customerService.updateCustomer(id, customerUpdateRequest);
    }

    @PostMapping(
            value = "{customerId}/profile-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public void uploadCustomerProfileImage(
            @PathVariable("customerId") Integer customerId,
            @RequestParam("file") MultipartFile file) {
        customerService.uploadCustomerProfileImage(customerId, file);
    }

    @GetMapping(value = "{customerId}/profile-image", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getCustomerProfileImage(
            @PathVariable("customerId") Integer customerId) {
        return customerService.getCustomerProfileImage(customerId);
    }
}
