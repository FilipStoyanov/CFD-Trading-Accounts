package com.t212.accounts.positions.gateways;

import com.t212.accounts.positions.lib.events.ClosePositionEvent;
import com.t212.accounts.positions.lib.events.OpenPositionEvent;
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

    public void sendOpenPositionEvent(OpenPositionEvent positionsEvent) {
        openPositionEvent.send(openPositionTopic, positionsEvent);
    }

    public void sendClosePositionEvent(ClosePositionEvent positionsEvent) {
        closePositionEvent.send(closePositionTopic, positionsEvent);
    }
}
