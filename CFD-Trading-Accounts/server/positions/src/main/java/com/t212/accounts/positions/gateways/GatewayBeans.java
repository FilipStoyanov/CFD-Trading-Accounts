package com.t212.accounts.positions.gateways;

import com.t212.accounts.positions.lib.events.ClosePositionEvent;
import com.t212.accounts.positions.lib.events.OpenPositionEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class GatewayBeans {

    @Value("${kafka.update-cfd-open-positions}")
    private String openPositionsTopic;

    @Value("${kafka.update-cfd-close-positions}")
    private String closePositionsTopic;

    public GatewayBeans() {
    }

    @Bean
    public KafkaGateway kafkaGateway(
            KafkaTemplate<String, OpenPositionEvent> openPositionsPublisher,
            KafkaTemplate<String, ClosePositionEvent> closePositionsPublisher) {
        return new KafkaGateway(
                openPositionsTopic, openPositionsPublisher, closePositionsTopic, closePositionsPublisher);
    }

    @Bean
    public NewTopic openPositionsTopicName() {
        return TopicBuilder.name(openPositionsTopic)
                .partitions(25)
                .replicas(3)
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT)
                .build();
    }

    @Bean
    public NewTopic closePositionsTopicName() {
        return TopicBuilder.name(closePositionsTopic)
                .partitions(25)
                .replicas(3)
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT)
                .build();
    }
}
