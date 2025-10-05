package com.starter.sample.controller;

import com.starter.sample.service.JwtProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtProvider jwtProvider;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        // In a real application, validate user credentials here

        // Create claims for the JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", request.email());
        claims.put("role", request.role().toUpperCase());

        // Generate JWT token
        String token = jwtProvider.generateToken(request.email(), claims);

        // Return token in response
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }
}

record LoginRequest(String email, String password, String role) {}

