package com.t212.cfdaccounts.cfdaccounts.api.websocket;

import com.t212.cfdaccounts.cfdaccounts.events.AccountBalanceUpdaterEvent;
import com.t212.cfdaccounts.cfdaccounts.serviceclient.AccountBalanceClient;
import com.t212.cfdaccounts.cfdaccounts.serviceclient.models.AccountBalance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.sql.Date;

@Component
public class AccountBalanceHandler {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public final AccountBalanceClient accountBalanceClient;

    public AccountBalanceHandler(AccountBalanceClient accountBalanceClient) {
        this.accountBalanceClient = accountBalanceClient;
    }

    @KafkaListener(
            topics = "balance.cfd",
            groupId = "cfd_user_account_balance",
            containerFactory = "accountBalanceUpdatedContainerFactory")
    void listenForAccountBalance(AccountBalanceUpdaterEvent data) {
        AccountBalance balance = new AccountBalance(data.balance(), new Date(data.timestamp()));
        messagingTemplate.convertAndSend("/cfd/balance/" + data.userId(), balance);
    }

}
