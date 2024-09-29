package com.example.bank.service;

import com.example.bank.dto.GenericResponse;
import com.example.bank.dto.PINRequest;
import org.springframework.http.ResponseEntity;

public interface AccountService {
    ResponseEntity<GenericResponse> createAccount(String emailFromToken);

    ResponseEntity<GenericResponse> getAccountInfo(Long id, String emailFromToken);

    ResponseEntity<GenericResponse> updateAccountPIN(Long id, String emailFromToken, PINRequest request);

    ResponseEntity<GenericResponse> toggleAccountStatus(Long id, String emailFromToken);
}
