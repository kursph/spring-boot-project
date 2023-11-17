package com.kursph.auth;

public record AuthenticationRequest(
        String username,
        String password
) {
}
