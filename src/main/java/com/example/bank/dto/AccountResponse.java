package com.example.bank.dto;

import com.example.bank.common.AccountStatus;
import com.example.bank.entity.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
    private Long id;

    private String cardNumber;

    private String cvv;

    private String PIN;

    private Double balance;

    private String cardholderName;

    private String accountStatus;
}
