package com.t212.instruments.instruments.manager.gateways;

import com.t212.instruments.instruments.manager.lib.events.StockPriceUpdateEvents;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaGateway {
    private final KafkaTemplate<String, StockPriceUpdateEvents> stockPriceUpdateEvent;
    private final String stockPriceUpdateTopic;

    public KafkaGateway(
            String stockPriceUpdateTopic,
            KafkaTemplate<String, StockPriceUpdateEvents> stockPriceUpdateEvent
    ) {
        this.stockPriceUpdateTopic = stockPriceUpdateTopic;
        this.stockPriceUpdateEvent = stockPriceUpdateEvent;
    }
}