package com.example.bank.controller;

import com.example.bank.dto.GenericResponse;
import com.example.bank.repository.UserRepository;
import com.example.bank.security.JwtTokenProvider;
import com.example.bank.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<GenericResponse> getUserProfile(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String emailFromToken = jwtTokenProvider.getEmailFromJwt(token);

        return userService.getUserProfile(emailFromToken);
    }

    @GetMapping("/accounts")
    public ResponseEntity<GenericResponse> getAllAccountsOfUser(@RequestHeader("Authorization") String authorizationHeader){
        String token = authorizationHeader.substring(7);
        String emailFromToken = jwtTokenProvider.getEmailFromJwt(token);

        return userService.getAllAccountsOfUser(emailFromToken);
    }
}
