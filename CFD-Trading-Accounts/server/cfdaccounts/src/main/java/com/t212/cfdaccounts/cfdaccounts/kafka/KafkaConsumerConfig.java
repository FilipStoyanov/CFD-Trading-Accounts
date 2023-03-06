package com.t212.cfdaccounts.cfdaccounts.kafka;

import com.t212.cfdaccounts.cfdaccounts.events.models.AccountBalanceDeserializer;
import com.t212.cfdaccounts.cfdaccounts.events.AccountBalanceUpdaterEvent;
import com.t212.cfdaccounts.cfdaccounts.events.PositionUpdateEvent;
import com.t212.cfdaccounts.cfdaccounts.events.StockPriceUpdateEvent;
import com.t212.cfdaccounts.cfdaccounts.events.models.PositionDeserializer;
import com.t212.cfdaccounts.cfdaccounts.events.models.StockPriceDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    private Environment environment;
    public Map<String, Object> consumerConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StockPriceDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        return props;
    }

    public Map<String, Object> consumerPositionConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, PositionDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        return props;
    }

    public Map<String, Object> consumerAccountConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, AccountBalanceDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        return props;
    }

    public ConsumerFactory<String, StockPriceUpdateEvent> stockPriceUpdateConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                consumerConfig());
    }

    public ConsumerFactory<String, PositionUpdateEvent> positionUpdateConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                consumerPositionConfig());
    }

    public ConsumerFactory<String, AccountBalanceUpdaterEvent> accountBalanceUpdateConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                consumerAccountConfig());
    }

    @Bean
    public KafkaListenerContainerFactory<
            ConcurrentMessageListenerContainer<String, StockPriceUpdateEvent>> StockPricesUpdatedContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, StockPriceUpdateEvent> f =
                new ConcurrentKafkaListenerContainerFactory<>();
        f.setConsumerFactory(stockPriceUpdateConsumerFactory());
        f.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return f;
    }

    @Bean
    public KafkaListenerContainerFactory<
            ConcurrentMessageListenerContainer<String, PositionUpdateEvent>> PositionsUpdatedContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PositionUpdateEvent> f =
                new ConcurrentKafkaListenerContainerFactory<>();
        f.setConsumerFactory(positionUpdateConsumerFactory());
        f.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return f;
    }

    @Bean
    public KafkaListenerContainerFactory<
            ConcurrentMessageListenerContainer<String, AccountBalanceUpdaterEvent>> accountBalanceUpdatedContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AccountBalanceUpdaterEvent> f =
                new ConcurrentKafkaListenerContainerFactory<>();
        f.setConsumerFactory(accountBalanceUpdateConsumerFactory());
        f.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return f;
    }
}