package com.t212.instruments.instruments.manager.bin.beans.kafka;

import com.t212.instruments.instruments.manager.lib.events.StockPriceUpdateEvents;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
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
import org.springframework.kafka.support.serializer.JsonSerializer;

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
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        return props;
    }

    public ConsumerFactory<String, StockPriceUpdateEvents> stockPriceUpdateConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                consumerConfig(), new StringDeserializer(), new JsonDeserializer<>(StockPriceUpdateEvents.class));
    }

    @Bean
    public KafkaListenerContainerFactory<
            ConcurrentMessageListenerContainer<String, StockPriceUpdateEvents>> StockPricesUpdatedContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, StockPriceUpdateEvents> f =
                new ConcurrentKafkaListenerContainerFactory<>();
        f.setConsumerFactory(stockPriceUpdateConsumerFactory());
        return f;
    }
}
