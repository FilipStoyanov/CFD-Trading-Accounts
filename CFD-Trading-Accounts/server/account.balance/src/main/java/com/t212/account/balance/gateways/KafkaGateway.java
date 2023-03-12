package com.t212.account.balance.gateways;

import com.t212.account.balance.events.AccountBalanceUpdaterEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaGateway {
    private final KafkaTemplate<String, AccountBalanceUpdaterEvent> accountBalanceUpdatedEvent;
    private final String accountBalanceTopic;

    public KafkaGateway(
            String accountBalanceTopic,
            KafkaTemplate<String, AccountBalanceUpdaterEvent> accountBalanceUpdatedEvent
    ) {
        this.accountBalanceTopic = accountBalanceTopic;
        this.accountBalanceUpdatedEvent = accountBalanceUpdatedEvent;
    }

    public void sendAccountBalanceUpdateEvent(String key, AccountBalanceUpdaterEvent accountBalanceUpdaterEvent) {
        accountBalanceUpdatedEvent.send(new ProducerRecord<>(
                accountBalanceTopic,
                key, accountBalanceUpdaterEvent));
    }
}
