package com.t212.cfdaccounts.cfdaccounts.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public record AccountBalanceUpdaterEvent(long userId, BigDecimal balance, Long timestamp) implements Serializable {
    @JsonCreator
    public AccountBalanceUpdaterEvent(
            @JsonProperty("userId") long userId,
            @JsonProperty("balance") BigDecimal balance,
            @JsonProperty("timestamp") Long timestamp) {
        this.userId = userId;
        this.balance = balance;
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AccountBalanceUpdaterEvent) obj;
        return Objects.equals(this.userId, that.userId) &&
                Objects.equals(this.balance, that.balance) &&
                Objects.equals(this.timestamp, that.timestamp);
    }

    @Override
    public String toString() {
        return "Message[" +
                "userId=" + userId + ", " +
                "balance=" + balance + ", " +
                "timestamp=" + timestamp + "]";
    }
}

