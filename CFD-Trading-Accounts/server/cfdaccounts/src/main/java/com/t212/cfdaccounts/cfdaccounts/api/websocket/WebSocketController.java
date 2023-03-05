package com.t212.cfdaccounts.cfdaccounts.api.websocket;

import com.t212.cfdaccounts.cfdaccounts.api.websocket.models.ClientGraphicMessage;
import com.t212.cfdaccounts.cfdaccounts.api.websocket.models.ClientInstrumentPage;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    private final MostUsedInstruments mostUsedInstruments;

    public WebSocketController(MostUsedInstruments mostUsedInstruments) {
        this.mostUsedInstruments = mostUsedInstruments;
    }

    @MessageMapping("/{id}/instruments")
    public void handleMessageForInstrumentsPagination(@DestinationVariable long id, @Payload ClientInstrumentPage message) {
        mostUsedInstruments.addPageToUser(id, message.getPage());
    }

    @MessageMapping("/{id}/graphic")
    public void handleMessageForInstrumentGraphic(@DestinationVariable long id, @Payload ClientGraphicMessage message) {
        mostUsedInstruments.addGraphicForUser(id, message.getInstrumentName());
    }
}
