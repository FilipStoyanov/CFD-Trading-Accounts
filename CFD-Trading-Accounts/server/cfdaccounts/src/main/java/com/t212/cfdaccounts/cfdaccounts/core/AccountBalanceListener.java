package com.t212.cfdaccounts.cfdaccounts.core;

import com.t212.cfdaccounts.cfdaccounts.events.AccountBalanceUpdaterEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AccountBalanceListener {

    public Map<Long, AccountBalanceUpdaterEvent> accountBalanceEvents;

    public AccountBalanceListener() {
        this.accountBalanceEvents = new ConcurrentHashMap<>();
    }

    @KafkaListener(
            topics = "balance.cfd",
            groupId = "cfd_account_balance_updates",
            containerFactory = "accountBalanceUpdatedContainerFactory")
    void listenForAccountBalanceUpdate(AccountBalanceUpdaterEvent data) {
        accountBalanceEvents.put(data.userId, data);
    }

    public Map<Long, AccountBalanceUpdaterEvent> getAccountBalanceEvents() {
        return accountBalanceEvents;
    }

    public AccountBalanceUpdaterEvent getAccountBalanceEventForUser(long userId) {
        return accountBalanceEvents.get(userId);
    }
}
