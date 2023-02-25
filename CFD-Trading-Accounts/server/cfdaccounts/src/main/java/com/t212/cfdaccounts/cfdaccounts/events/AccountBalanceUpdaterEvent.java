package com.t212.cfdaccounts.cfdaccounts.events;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.t212.cfdaccounts.cfdaccounts.events.models.AccountBalanceType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class AccountBalanceUpdaterEvent implements Serializable {
    public final long userId;
    public final BigDecimal balance;
    public final AccountBalanceType type;
    public final Long timestamp;

    @JsonCreator
    public AccountBalanceUpdaterEvent(
            @JsonProperty("userId") long userId,
            @JsonProperty("balance") BigDecimal balance,
            @JsonProperty("type") AccountBalanceType type,
            @JsonProperty("timestamp") Long timestamp) {
        this.userId = userId;
        this.balance = balance;
        this.type = type;
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AccountBalanceUpdaterEvent) obj;
        return Objects.equals(this.userId, that.userId) &&
                Objects.equals(this.balance, that.balance) &&
                Objects.equals(this.type, that.type) &&
                Objects.equals(this.timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.userId, this.balance, this.type, this.timestamp);
    }

    @Override
    public String toString() {
        return "Message[" +
                "userId=" + userId + ", " +
                "type=" + type + ", " +
                "balance=" + balance + ", " +
                "timestamp=" + timestamp + "]";
    }
}
