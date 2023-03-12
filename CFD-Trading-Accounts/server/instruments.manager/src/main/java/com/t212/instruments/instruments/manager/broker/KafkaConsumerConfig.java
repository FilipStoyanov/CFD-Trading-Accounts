package com.t212.instruments.instruments.manager.broker;

import com.t212.instruments.instruments.manager.lib.events.StockPriceUpdateEvents;
import com.t212.instruments.instruments.manager.lib.events.models.StockPriceDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
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
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "10000");
        return props;
    }

    public ConsumerFactory<String, StockPriceUpdateEvents> stockPriceUpdateConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                consumerConfig());
    }
    @Bean
    public KafkaListenerContainerFactory<
            ConcurrentMessageListenerContainer<String, StockPriceUpdateEvents>> StockPricesUpdatedContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, StockPriceUpdateEvents> f =
                new ConcurrentKafkaListenerContainerFactory<>();
        f.setConsumerFactory(stockPriceUpdateConsumerFactory());
        f.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return f;
    }
}
