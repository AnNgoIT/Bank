package com.example.bank.controller;

import com.example.bank.dto.GenericResponse;
import com.example.bank.dto.PINRequest;
import com.example.bank.security.JwtTokenProvider;
import com.example.bank.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {
    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    AccountService accountService;

    @PostMapping("")
    public ResponseEntity<GenericResponse> createAccount(@RequestHeader("Authorization") String authorizationHeader){
        String token = authorizationHeader.substring(7);
        String emailFromToken = jwtTokenProvider.getEmailFromJwt(token);

        return accountService.createAccount(emailFromToken);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse> getAccountInfo(@PathVariable Long id,
                                                          @RequestHeader("Authorization") String authorizationHeader){
        String token = authorizationHeader.substring(7);
        String emailFromToken = jwtTokenProvider.getEmailFromJwt(token);

        return accountService.getAccountInfo(id, emailFromToken);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<GenericResponse> updateAccountPIN(@PathVariable Long id,
                                                          @Valid @RequestBody PINRequest request,
                                                          @RequestHeader("Authorization") String authorizationHeader){
        String token = authorizationHeader.substring(7);
        String emailFromToken = jwtTokenProvider.getEmailFromJwt(token);

        return accountService.updateAccountPIN(id, emailFromToken, request);
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<GenericResponse> toggleAccountStatus(@PathVariable Long id,
                                                            @RequestHeader("Authorization") String authorizationHeader){
        String token = authorizationHeader.substring(7);
        String emailFromToken = jwtTokenProvider.getEmailFromJwt(token);

        return accountService.toggleAccountStatus(id, emailFromToken);
    }
}
