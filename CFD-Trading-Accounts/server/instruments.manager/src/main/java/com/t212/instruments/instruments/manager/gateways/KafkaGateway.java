package com.t212.instruments.instruments.manager.gateways;

import com.t212.instruments.instruments.manager.lib.events.StockPriceUpdateEvents;
import org.springframework.kafka.core.KafkaTemplate;
public class KafkaGateway {
    private final KafkaTemplate<String, StockPriceUpdateEvents> stockPriceUpdateEvent;
    private final String stockPriceUpdateTopic;
    private final long stockPriceUpdateTopicCnt;

    public KafkaGateway(
            String stockPriceUpdateTopic,
            Integer stockPriceUpdateTopicCnt,
            KafkaTemplate<String, StockPriceUpdateEvents> stockPriceUpdateEvent
    ) {
        this.stockPriceUpdateTopic = stockPriceUpdateTopic;
        this.stockPriceUpdateTopicCnt = stockPriceUpdateTopicCnt;
        this.stockPriceUpdateEvent = stockPriceUpdateEvent;
    }

    public void sendStockPriceUpdateEvent(StockPriceUpdateEvents stockPrice) {
        stockPriceUpdateEvent.send(stockPriceUpdateTopic, stockPrice);
    }
}