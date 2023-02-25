package com.t212.account.balance.gateways;

import com.t212.account.balance.lib.events.AccountBalanceUpdaterEvent;
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

    public void sendAccountBalanceUpdateEvent(AccountBalanceUpdaterEvent accountBalanceUpdaterEvent) {
        accountBalanceUpdatedEvent.send(accountBalanceTopic, accountBalanceUpdaterEvent);
    }
}
