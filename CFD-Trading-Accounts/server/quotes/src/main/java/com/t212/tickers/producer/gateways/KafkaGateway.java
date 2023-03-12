package com.t212.tickers.producer.gateways;

import com.t212.tickers.producer.lib.events.StockPriceUpdateEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaGateway {
    private final KafkaTemplate<String, StockPriceUpdateEvent> stockPriceUpdateEvent;
    @Value("${kafka.stock-prices-topic}")
    private final String stockPriceUpdateTopic;

    public KafkaGateway(
            String stockPriceUpdateTopic,
            KafkaTemplate<String, StockPriceUpdateEvent> stockPriceUpdateEvent
            ) {
        this.stockPriceUpdateTopic = stockPriceUpdateTopic;
        this.stockPriceUpdateEvent = stockPriceUpdateEvent;
    }

    public void sendStockPriceUpdateEvent(String topicName, String key, StockPriceUpdateEvent stockPrice) {
        stockPriceUpdateEvent.send(new ProducerRecord<>(topicName, key, stockPrice));
    }
}



