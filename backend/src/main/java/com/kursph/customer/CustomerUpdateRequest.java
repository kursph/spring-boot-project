package com.kursph.customer;

public record CustomerUpdateRequest(
        String name,
        String email,
        Integer age
) {
}
