package com.t212.accounts.positions.bin.beans.kafka;

import com.t212.accounts.positions.lib.events.ClosePositionEvent;
import com.t212.accounts.positions.lib.events.ClosePositionSerializer;
import com.t212.accounts.positions.lib.events.OpenPositionEvent;
import com.t212.accounts.positions.lib.events.OpenPositionSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaProducerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KafkaTemplate<String, OpenPositionEvent> openPositionPublisher(
            ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<>(
                new DefaultKafkaProducerFactory<>(producerOpenPositionConfig()));
    }

    @Bean
    public KafkaTemplate<String, ClosePositionEvent> closePositionPublisher(
            ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<>(
                new DefaultKafkaProducerFactory<>(producerClosePositionConfig()));
    }

    private Map<String, Object> producerOpenPositionConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, OpenPositionSerializer.class);
        return props;
    }

    private Map<String, Object> producerClosePositionConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ClosePositionSerializer.class);
        return props;
    }
}
