package com.t212.account.balance.api.rest;

import com.t212.account.balance.api.rest.models.BalanceInput;
import com.t212.account.balance.core.models.AccountBalance;
import com.t212.account.balance.events.AccountBalanceUpdaterEvent;
import com.t212.account.balance.gateways.KafkaGateway;
import com.t212.account.balance.api.rest.models.ApiResponse;
import com.t212.account.balance.core.AccountBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/")
public class AccountBalanceController {
    @Autowired
    private KafkaGateway kafkaGateway;
    private final AccountBalanceService accountBalanceService;

    public AccountBalanceController(AccountBalanceService accountBalanceService) {
        this.accountBalanceService = accountBalanceService;
    }

    @GetMapping(value = "users/{userId}/balance")
    public ResponseEntity<ApiResponse> getBalance(@PathVariable long userId) {
        if (userId < 1) {
            return ResponseEntity.status(400).body(new ApiResponse(400, "Invalid path variable"));
        }
        try {
            AccountBalance accountBalance = accountBalanceService.getBalanceByUserId(userId);
            return ResponseEntity.status(200).body(new ApiResponse(200, "", accountBalance));
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(400).body(new ApiResponse(400, "Something bad wrong"));
        }
    }

    @PutMapping(value = "users/{userId}/withdraw")
    public ResponseEntity<ApiResponse> withdraw(@PathVariable long userId, @RequestBody BalanceInput balance) {
        if (userId < 1 || balance.amount.compareTo(new BigDecimal(1)) < 0) {
            return ResponseEntity.status(400).body(new ApiResponse(400, "Invalid path variable"));
        }
        try {
            AccountBalance accountBalance = accountBalanceService.withdraw(userId, balance.amount);
            AccountBalanceUpdaterEvent pEvent = new AccountBalanceUpdaterEvent(userId, accountBalance.balance(), System.currentTimeMillis());
            kafkaGateway.sendAccountBalanceUpdateEvent(String.valueOf(userId), pEvent);
            return ResponseEntity.status(200).body(new ApiResponse(200, "Successfully updated", accountBalance));
        } catch (DataAccessException e) {
            return ResponseEntity.status(404).body(new ApiResponse(404, "Not found"));
        }
    }

    @PutMapping(value = "users/{userId}/deposit")
    public ResponseEntity<ApiResponse> deposit(@PathVariable long userId, @RequestBody BalanceInput deposit) {
        if (userId < 1) {
            return ResponseEntity.status(400).body(new ApiResponse(400, "Invalid path variable"));
        }
        try {
            AccountBalance accountBalance = accountBalanceService.deposit(userId, deposit.amount);
            AccountBalanceUpdaterEvent pEvent = new AccountBalanceUpdaterEvent(userId, accountBalance.balance(), System.currentTimeMillis());
            kafkaGateway.sendAccountBalanceUpdateEvent(String.valueOf(userId), pEvent);
            return ResponseEntity.status(200).body(new ApiResponse(200, "Successfully updated balance", accountBalance));
        } catch (DataAccessException e) {
            return ResponseEntity.status(404).body(new ApiResponse(404, "Not found"));
        }
    }
}