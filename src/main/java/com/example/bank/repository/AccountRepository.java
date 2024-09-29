package com.example.bank.repository;

import com.example.bank.entity.Account;
import com.example.bank.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByCardNumber(String cardNumber);

    List<Account> findAllByCardholder(User user);

    Optional<Account> findByCardholderAndId(User user, Long id);
}
