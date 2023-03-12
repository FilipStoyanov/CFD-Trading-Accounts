package com.t212.accounts.positions.gateways;

import com.t212.accounts.positions.lib.events.ClosePositionEvent;
import com.t212.accounts.positions.lib.events.OpenPositionEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaGateway {
    private final KafkaTemplate<String, OpenPositionEvent> openPositionEvent;

    private final String openPositionTopic;
    private final KafkaTemplate<String, ClosePositionEvent> closePositionEvent;
    private final String closePositionTopic;

    public KafkaGateway(
            String openPositionTopic,
            KafkaTemplate<String, OpenPositionEvent> openPositionEvent,
            String closePositionTopic,
            KafkaTemplate<String, ClosePositionEvent> closePositionEvent
    ) {
        this.openPositionTopic = openPositionTopic;
        this.openPositionEvent = openPositionEvent;
        this.closePositionTopic = closePositionTopic;
        this.closePositionEvent = closePositionEvent;
    }

    public void sendOpenPositionEvent(String key, OpenPositionEvent positionsEvent) {
        openPositionEvent.send(new ProducerRecord<>(openPositionTopic, key, positionsEvent));
    }

    public void sendClosePositionEvent(String key, ClosePositionEvent positionsEvent) {
        closePositionEvent.send(new ProducerRecord<>(closePositionTopic, key, positionsEvent));
    }
}
