package com.t212.cfdaccounts.cfdaccounts.api.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.t212.cfdaccounts.cfdaccounts.events.AccountBalanceUpdaterEvent;
import com.t212.cfdaccounts.cfdaccounts.serviceclient.AccountBalanceClient;
import com.t212.cfdaccounts.cfdaccounts.serviceclient.models.AccountBalance;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import java.sql.Date;

@Component
public class AccountBalanceHandler {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountBalanceHandler.class);
    public final AccountBalanceClient accountBalanceClient;

    public AccountBalanceHandler(AccountBalanceClient accountBalanceClient) {
        this.accountBalanceClient = accountBalanceClient;
    }

    @RetryableTopic(
            attempts = "3",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            include = {Exception.class}
    )
    @KafkaListener(
            topics = "balance.cfd",
            groupId = "cfd_user_account_balance-#{ T(java.util.UUID).randomUUID().toString() }",
            containerFactory = "accountBalanceUpdatedContainerFactory")
    void listenForAccountBalance(AccountBalanceUpdaterEvent data) {
        if (data.timestamp() != null && data.balance() != null) {
            AccountBalance balance = new AccountBalance(data.balance(), new Date(data.timestamp()));
            messagingTemplate.convertAndSend("/cfd/balance/" + data.userId(), balance);
        }
    }

    @DltHandler
    public void handleDlt(ConsumerRecord<String, AccountBalanceUpdaterEvent> record, Acknowledgment
            acknowledgment, Consumer<?, ?> consumer) {
        if (record.value() != null && record.value().balance() != null && record.value().userId() > 0) {
            try {
                AccountBalance balance = accountBalanceClient.getAccountBalance(record.value().userId());
                messagingTemplate.convertAndSend("/cfd/balance/" + record.value().userId(), balance);
            } catch (JsonProcessingException | ResourceAccessException e) {
                LOGGER.warn("Exception occurred:", e);
            }
        }
        acknowledgment.acknowledge();
    }
}
