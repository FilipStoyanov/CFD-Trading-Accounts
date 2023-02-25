package com.t212.account.balance.gateways;

import com.t212.account.balance.lib.events.AccountBalanceUpdaterEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class GatewayBeans {
    @Value("${kafka.update-cfd-balancer}")
    private String accountBalanceTopic;

    public GatewayBeans() {
    }

    @Bean
    public KafkaGateway kafkaGateway(
            KafkaTemplate<String, AccountBalanceUpdaterEvent> accountBalancePublisher) {
        return new KafkaGateway(
                accountBalanceTopic, accountBalancePublisher);
    }

    @Bean
    public NewTopic updateBalanceTopic() {
        return new NewTopic(accountBalanceTopic, 10, (short) 1);
    }

}
