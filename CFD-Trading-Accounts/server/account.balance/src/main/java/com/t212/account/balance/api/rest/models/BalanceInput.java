package com.t212.account.balance.api.rest.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class BalanceInput {
    public BigDecimal amount;
    @JsonCreator
    public BalanceInput(@JsonProperty("amount") BigDecimal amount) {
        this.amount = amount;
    }
}