package com.t212.cfdaccounts.cfdaccounts.core;

import com.t212.cfdaccounts.cfdaccounts.api.websocket.models.Message;
import com.t212.cfdaccounts.cfdaccounts.api.websocket.models.WebSocketClient;

public final class Mappers {
    public static Message fromClientToMessage(WebSocketClient client) {
        return new Message(client.getOpenPositions());
    }
}
