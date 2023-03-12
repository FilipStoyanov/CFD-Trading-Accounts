package com.t212.instruments.instruments.manager.gateways;

import com.t212.instruments.instruments.manager.lib.events.StockPriceUpdateEvents;
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
            KafkaTemplate<String, StockPriceUpdateEvents> stockPriceUpdatePublisher) {
        return new KafkaGateway(
                stockPriceUpdateTopic, stockPriceUpdatePublisher);
    }

}
