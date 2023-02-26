package com.t212.cfdaccounts.cfdaccounts.kafka;

import com.t212.cfdaccounts.cfdaccounts.events.models.AccountBalanceDeserializer;
import com.t212.cfdaccounts.cfdaccounts.events.AccountBalanceUpdaterEvent;
import com.t212.cfdaccounts.cfdaccounts.events.PositionUpdateEvent;
import com.t212.cfdaccounts.cfdaccounts.events.StockPriceUpdateEvent;
import com.t212.cfdaccounts.cfdaccounts.events.models.PositionDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    public Map<String, Object> consumerConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        return props;
    }

    public Map<String, Object> consumerPositionConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, PositionDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        return props;
    }

    public Map<String, Object> consumerAccountConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, AccountBalanceDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        return props;
    }

    public ConsumerFactory<String, StockPriceUpdateEvent> stockPriceUpdateConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                consumerConfig(), new StringDeserializer(), new JsonDeserializer<>(StockPriceUpdateEvent.class));
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
        return f;
    }

    @Bean
    public KafkaListenerContainerFactory<
            ConcurrentMessageListenerContainer<String, PositionUpdateEvent>> PositionsUpdatedContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PositionUpdateEvent> f =
                new ConcurrentKafkaListenerContainerFactory<>();
        f.setConsumerFactory(positionUpdateConsumerFactory());
        return f;
    }

    @Bean
    public KafkaListenerContainerFactory<
            ConcurrentMessageListenerContainer<String, AccountBalanceUpdaterEvent>> accountBalanceUpdatedContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AccountBalanceUpdaterEvent> f =
                new ConcurrentKafkaListenerContainerFactory<>();
        f.setConsumerFactory(accountBalanceUpdateConsumerFactory());
        return f;
    }
}