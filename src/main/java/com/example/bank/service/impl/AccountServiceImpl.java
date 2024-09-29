package com.example.bank.service.impl;

import com.example.bank.common.AccountStatus;
import com.example.bank.dto.AccountResponse;
import com.example.bank.dto.GenericResponse;
import com.example.bank.dto.PINRequest;
import com.example.bank.entity.Account;
import com.example.bank.entity.User;
import com.example.bank.repository.AccountRepository;
import com.example.bank.repository.UserRepository;
import com.example.bank.service.AccountService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    AccountRepository accountRepository;

    @Override
    public ResponseEntity<GenericResponse> createAccount(String emailFromToken) {
        try {
            Optional<User> userOptional = userRepository.findByEmail(emailFromToken);
            if (userOptional.isPresent()) {
                User user = userOptional.get();

                Account account = new Account();
                account.setCardNumber(generateUniqueCardNumber());
                account.setCvv(generateRandomCVV());
                account.setBalance((double) 0);
                account.setCardholder(user);
                account.setStatus(AccountStatus.ACTIVE);

                accountRepository.save(account);

                return ResponseEntity.status(HttpStatus.OK).body(
                        GenericResponse.builder()
                                .success(true)
                                .message("Create account successfully")
                                .result("")
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
    public ResponseEntity<GenericResponse> getAccountInfo(Long id, String emailFromToken) {
        try {
            Optional<User> userOptional = userRepository.findByEmail(emailFromToken);
            if (userOptional.isPresent()) {
                User user = userOptional.get();

                Optional<Account> accountOptional = accountRepository.findByCardholderAndId(user, id);
                if (accountOptional.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                            GenericResponse.builder()
                                    .success(false)
                                    .message("The account does not exist or does not belong to you")
                                    .result("")
                                    .statusCode(HttpStatus.BAD_REQUEST.value())
                                    .build()
                    );
                }

                Account account = accountOptional.get();

                AccountResponse accountResponse = new AccountResponse();
                BeanUtils.copyProperties(account, accountResponse);
                accountResponse.setCardholderName(account.getCardholder().getFullname());
                accountResponse.setAccountStatus(account.getStatus().name());

                return ResponseEntity.status(HttpStatus.OK).body(
                        GenericResponse.builder()
                                .success(true)
                                .message("Get account info successfully")
                                .result(accountResponse)
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
    public ResponseEntity<GenericResponse> updateAccountPIN(Long id, String emailFromToken, PINRequest request) {
        try {
            Optional<User> userOptional = userRepository.findByEmail(emailFromToken);
            if (userOptional.isPresent()) {
                User user = userOptional.get();

                Optional<Account> accountOptional = accountRepository.findByCardholderAndId(user, id);
                if (accountOptional.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                            GenericResponse.builder()
                                    .success(false)
                                    .message("The account does not exist or does not belong to you")
                                    .result("")
                                    .statusCode(HttpStatus.BAD_REQUEST.value())
                                    .build()
                    );
                }

                Account account = accountOptional.get();
                account.setPIN(request.getPin());
                accountRepository.save(account);

                return ResponseEntity.status(HttpStatus.OK).body(
                        GenericResponse.builder()
                                .success(true)
                                .message("Updated account PIN successfully")
                                .result("")
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
    public ResponseEntity<GenericResponse> toggleAccountStatus(Long id, String emailFromToken) {
        try {
            Optional<User> userOptional = userRepository.findByEmail(emailFromToken);
            if (userOptional.isPresent()) {
                User user = userOptional.get();

                Optional<Account> accountOptional = accountRepository.findByCardholderAndId(user, id);
                if (accountOptional.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                            GenericResponse.builder()
                                    .success(false)
                                    .message("The account does not exist or does not belong to you")
                                    .result("")
                                    .statusCode(HttpStatus.BAD_REQUEST.value())
                                    .build()
                    );
                }

                Account account = accountOptional.get();
                if (account.getStatus().equals(AccountStatus.ACTIVE)) {
                    account.setStatus(AccountStatus.BLOCKED);
                } else {
                    account.setStatus(AccountStatus.ACTIVE);
                }
                accountRepository.save(account);

                return ResponseEntity.status(HttpStatus.OK).body(
                        GenericResponse.builder()
                                .success(true)
                                .message("Toggled account status successfully")
                                .result("")
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

    public String generateUniqueCardNumber() {
        String cardNumber;
        do {
            cardNumber = RandomStringUtils.randomNumeric(16);
        } while (accountRepository.existsByCardNumber(cardNumber));
        return cardNumber;
    }

    public String generateRandomCVV() {
        return RandomStringUtils.randomNumeric(3);
    }
}
