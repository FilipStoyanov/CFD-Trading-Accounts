package com.t212.tickers.producer.gateways;

import com.t212.tickers.producer.lib.events.StockPriceUpdateEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;

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
        return TopicBuilder.name(stockPriceUpdateTopic)
                .partitions(25)
                .replicas(3)
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT)
                .configs(new HashMap<String, String>() {{
                    put("retention.ms", "5000");
                }})
                .build();
    }
}
