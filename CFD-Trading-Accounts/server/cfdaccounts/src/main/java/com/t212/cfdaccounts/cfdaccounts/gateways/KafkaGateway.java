package com.t212.cfdaccounts.cfdaccounts.gateways;

import com.t212.cfdaccounts.cfdaccounts.events.AccountBalanceUpdaterEvent;
import com.t212.cfdaccounts.cfdaccounts.events.PositionUpdateEvent;
import com.t212.cfdaccounts.cfdaccounts.events.StockPriceUpdateEvent;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaGateway {
    private final KafkaTemplate<String, StockPriceUpdateEvent> stockPriceUpdateEvent;
    private final KafkaTemplate<String, PositionUpdateEvent> positionUpdatedEvent;
    private final KafkaTemplate<String, AccountBalanceUpdaterEvent> accountBalanceUpdatedEvent;

    private final String stockPriceUpdateTopic;
    private final long stockPriceUpdateTopicCnt;
    private final String positionsUpdatedTopic;
    private final String accountBalanceTopic;

    public KafkaGateway(
            String stockPriceUpdateTopic,
            Integer stockPriceUpdateTopicCnt,
            KafkaTemplate<String, StockPriceUpdateEvent> stockPriceUpdateEvent,
            String positionsUpdatedTopic,
            KafkaTemplate<String, PositionUpdateEvent> positionsUpdatedEvent,
            String accountBalanceTopic,
            KafkaTemplate<String, AccountBalanceUpdaterEvent> accountBalanceUpdatedEvent
    ) {
        this.stockPriceUpdateTopic = stockPriceUpdateTopic;
        this.stockPriceUpdateTopicCnt = stockPriceUpdateTopicCnt;
        this.stockPriceUpdateEvent = stockPriceUpdateEvent;
        this.positionUpdatedEvent = positionsUpdatedEvent;
        this.positionsUpdatedTopic = positionsUpdatedTopic;
        this.accountBalanceTopic = accountBalanceTopic;
        this.accountBalanceUpdatedEvent = accountBalanceUpdatedEvent;
    }

    public void sendStockPriceUpdateEvent(StockPriceUpdateEvent stockPrice) {
        stockPriceUpdateEvent.send(stockPriceUpdateTopic, stockPrice);
    }

    public void sendPositionUpdateEvent(PositionUpdateEvent positionsEvent) {
        positionUpdatedEvent.send(positionsUpdatedTopic, positionsEvent);
    }

    public void sendAccountBalanceUpdateEvent(AccountBalanceUpdaterEvent accountBalanceUpdaterEvent) {
        accountBalanceUpdatedEvent.send(accountBalanceTopic, accountBalanceUpdaterEvent);
    }
}