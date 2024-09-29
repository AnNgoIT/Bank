package com.example.bank.entity;

import com.example.bank.common.AccountStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "varchar(19) not null unique")
    private String cardNumber;

    @Column(columnDefinition = "char(3) not null")
    private String cvv;

    @Column(columnDefinition = "varchar(6)")
    private String PIN;

    @Column(nullable = false)
    private Double balance;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonBackReference
    private User cardholder;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;
}
