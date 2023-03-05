package com.t212.tickers.producer.gateways;

import com.t212.tickers.producer.lib.events.StockPriceUpdateEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class GatewayBeans {
    @Value("${kafka.stock-prices-topic}")
    private String stockPriceUpdateTopic;

    public GatewayBeans() {
    }

    @Bean
    public KafkaGateway kafkaGateway(
            KafkaTemplate<String, StockPriceUpdateEvent> stockPricesPublisher) {
        return new KafkaGateway(
                stockPriceUpdateTopic, stockPricesPublisher);
    }

    @Bean
    public NewTopic topicName() {
        return new NewTopic(stockPriceUpdateTopic, 10, (short) 1);
    }

}
