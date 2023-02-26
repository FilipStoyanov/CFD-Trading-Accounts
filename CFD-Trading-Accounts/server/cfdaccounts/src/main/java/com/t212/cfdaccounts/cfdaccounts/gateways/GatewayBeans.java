package com.t212.cfdaccounts.cfdaccounts.gateways;

import com.t212.cfdaccounts.cfdaccounts.events.AccountBalanceUpdaterEvent;
import com.t212.cfdaccounts.cfdaccounts.events.PositionUpdateEvent;
import com.t212.cfdaccounts.cfdaccounts.events.StockPriceUpdateEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class GatewayBeans {
    public final String stockPriceUpdateTopic = "quotes.raw.cfd";

    @Value("${kafka.stock-prices-topic-partitions}")
    private int stockPriceUpdateTopicCnt;

    @Value("${kafka.update-cfd-positions}")
    private String positionsUpdateTopic;

    @Value("${kafka.update-cfd-balance}")
    private String accountBalanceUpdateTopic;

    public GatewayBeans() {
    }

    @Bean
    public KafkaGateway kafkaGateway(
            KafkaTemplate<String, StockPriceUpdateEvent> stockPricesPublisher,
            KafkaTemplate<String, PositionUpdateEvent> positionsUpdatedPublisher,
            KafkaTemplate<String, AccountBalanceUpdaterEvent> accountBalanceUpdatedPublisher) {
        return new KafkaGateway(
                stockPriceUpdateTopic, stockPriceUpdateTopicCnt, stockPricesPublisher, positionsUpdateTopic, positionsUpdatedPublisher, accountBalanceUpdateTopic, accountBalanceUpdatedPublisher);
    }

    @Bean
    public NewTopic topicName() {
        return new NewTopic(stockPriceUpdateTopic, 10, (short) 1);
    }

    @Bean
    public NewTopic positionsTopicName() {
        return new NewTopic(positionsUpdateTopic, 10, (short) 1);
    }

    @Bean
    public NewTopic balanceTopicName() {
        return new NewTopic(accountBalanceUpdateTopic, 10, (short) 1);
    }
}
