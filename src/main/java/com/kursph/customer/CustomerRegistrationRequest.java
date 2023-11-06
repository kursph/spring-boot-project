package com.kursph.customer;

public record CustomerRegistrationRequest(
        String name,
        String email,
        Integer age
) {
}
