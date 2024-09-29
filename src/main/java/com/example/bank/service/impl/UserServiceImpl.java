package com.example.bank.service.impl;

import com.example.bank.common.Role;
import com.example.bank.dto.AccountResponse;
import com.example.bank.dto.GenericResponse;
import com.example.bank.dto.LoginRequest;
import com.example.bank.dto.RegisterRequest;
import com.example.bank.entity.Account;
import com.example.bank.entity.User;
import com.example.bank.repository.AccountRepository;
import com.example.bank.repository.UserRepository;
import com.example.bank.security.JwtTokenProvider;
import com.example.bank.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AccountRepository accountRepository;

    private static final String PHONE_NUMBER_REGEX = "^(\\+\\d{1,3}[- ]?)?\\d{10}$";

    @Override
    public ResponseEntity<GenericResponse> register(RegisterRequest request) {
        try {
            Optional<User> userOptionalEmail = userRepository.findByEmail(request.getEmail());
            if (userOptionalEmail.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        GenericResponse.builder()
                                .success(false)
                                .message("Email already in use")
                                .result("Conflict")
                                .statusCode(HttpStatus.CONFLICT.value())
                                .build()
                );
            }

            if (!isValidPhoneNumber(request.getPhone())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        GenericResponse.builder()
                                .success(false)
                                .message("Invalid phone number format")
                                .result("Bad request")
                                .statusCode(HttpStatus.BAD_REQUEST.value())
                                .build()
                );
            }

            Optional<User> userOptionalPhone = userRepository.findByPhone(request.getPhone());
            if (userOptionalPhone.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        GenericResponse.builder()
                                .success(false)
                                .message("Phone number already in use")
                                .result("Conflict")
                                .statusCode(HttpStatus.CONFLICT.value())
                                .build()
                );
            }

            if (!request.getConfirmPassword().equals(request.getPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        GenericResponse.builder()
                                .success(false)
                                .message("Password and Confirm Password don't match")
                                .result("Bad request")
                                .statusCode(HttpStatus.BAD_REQUEST.value())
                                .build()
                );
            }

            User user = new User();
            user.setFullname(request.getFullname());
            user.setEmail(request.getEmail());
            user.setPhone(request.getPhone());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(Role.CUSTOMER);

            userRepository.save(user);

            return ResponseEntity.status(HttpStatus.OK).body(
                    GenericResponse.builder()
                            .success(true)
                            .message("Register user successfully")
                            .result("")
                            .statusCode(HttpStatus.OK.value())
                            .build()
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    GenericResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .result("Internal server error")
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build()
            );
        }
    }

    @Override
    public ResponseEntity<GenericResponse> login(LoginRequest request) {
        try {
            Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        GenericResponse.builder()
                                .success(false)
                                .message("The account does not exist")
                                .result("Not found")
                                .statusCode(HttpStatus.NOT_FOUND.value())
                                .build());
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
            Map<String, String> map = new HashMap<>();
            map.put("accessToken", accessToken);
            map.put("role", String.valueOf(userOptional.get().getRole()));

            User user = userOptional.get();
            user.setLastLoginAt(new Date());
            userRepository.save(user);

            return ResponseEntity.ok().body(GenericResponse.builder()
                    .success(true)
                    .message("Login successfully")
                    .result(map)
                    .statusCode(HttpStatus.OK.value())
                    .build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    GenericResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .result("Email or Password is incorrect. Please type again!")
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build()
            );
        }
    }

    @Override
    public ResponseEntity<GenericResponse> getUserProfile(String emailFromToken) {
        try {
            Optional<User> userOptional = userRepository.findByEmail(emailFromToken);
            if (userOptional.isPresent()) {
                User user = userOptional.get();

                Map<String, String> info = new HashMap<>();
                info.put("fullname", user.getFullname());
                info.put("email", user.getEmail());
                info.put("phone", user.getPhone());
                info.put("role", String.valueOf(user.getRole()));

                return ResponseEntity.status(HttpStatus.OK).body(
                        GenericResponse.builder()
                                .success(true)
                                .message("Get your profile successfully")
                                .result(info)
                                .statusCode(HttpStatus.OK.value())
                                .build()
                );

            } else {
                return ResponseEntity.status(401)
                        .body(GenericResponse.builder()
                                .success(false)
                                .message("Unauthorized")
                                .result("Invalid token")
                                .statusCode(HttpStatus.UNAUTHORIZED.value())
                                .build());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    GenericResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .result("Internal server error")
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build()
            );
        }
    }

    @Override
    public ResponseEntity<GenericResponse> getAllAccountsOfUser(String emailFromToken) {
        try {
            Optional<User> userOptional = userRepository.findByEmail(emailFromToken);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                List<Account> accountList = accountRepository.findAllByCardholder(user);

                List<AccountResponse> accountResponseList = new ArrayList<>();
                for (Account account : accountList) {
                    AccountResponse accountResponse = new AccountResponse();
                    BeanUtils.copyProperties(account, accountResponse);
                    accountResponse.setCardholderName(account.getCardholder().getFullname());
                    accountResponse.setAccountStatus(account.getStatus().name());

                    accountResponseList.add(accountResponse);
                }

                return ResponseEntity.status(HttpStatus.OK).body(
                        GenericResponse.builder()
                                .success(true)
                                .message("Successfully retrieved all your accounts")
                                .result(accountResponseList)
                                .statusCode(HttpStatus.OK.value())
                                .build()
                );

            } else {
                return ResponseEntity.status(401)
                        .body(GenericResponse.builder()
                                .success(false)
                                .message("Unauthorized")
                                .result("Invalid token")
                                .statusCode(HttpStatus.UNAUTHORIZED.value())
                                .build());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    GenericResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .result("Internal server error")
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build()
            );
        }
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        Pattern pattern = Pattern.compile(PHONE_NUMBER_REGEX);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }
}
