package com.t212.cfdaccounts.cfdaccounts.kafka;

import com.t212.cfdaccounts.cfdaccounts.events.AccountBalanceUpdaterEvent;
import com.t212.cfdaccounts.cfdaccounts.events.PositionUpdateEvent;
import com.t212.cfdaccounts.cfdaccounts.events.StockPriceUpdateEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaProducerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KafkaTemplate<String, StockPriceUpdateEvent> stockPriceUpdatedPublisher(
            ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<>(
                new DefaultKafkaProducerFactory<>(producerConfig()));
    }

    @Bean
    public KafkaTemplate<String, PositionUpdateEvent> positionsUpdatedPublisher(
            ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<>(
                new DefaultKafkaProducerFactory<>(producerConfig()));
    }

    @Bean
    public KafkaTemplate<String, AccountBalanceUpdaterEvent> accountBalanceUpdatedPublisher(
            ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<>(
                new DefaultKafkaProducerFactory<>(producerConfig()));
    }

    private Map<String, Object> producerConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return props;
    }
}
