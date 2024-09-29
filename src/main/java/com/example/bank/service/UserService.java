package com.example.bank.service;

import com.example.bank.dto.GenericResponse;
import com.example.bank.dto.LoginRequest;
import com.example.bank.dto.RegisterRequest;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<GenericResponse> register(RegisterRequest request);

    ResponseEntity<GenericResponse> login(LoginRequest request);

    ResponseEntity<GenericResponse> getUserProfile(String emailFromToken);

    ResponseEntity<GenericResponse> getAllAccountsOfUser(String emailFromToken);
}
