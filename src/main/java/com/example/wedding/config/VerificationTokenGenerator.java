package com.example.wedding.config;

import java.util.UUID;

public class VerificationTokenGenerator {
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}

