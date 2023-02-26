package com.t212.cfdaccounts.cfdaccounts.api.websocket.models;

import com.t212.cfdaccounts.cfdaccounts.core.models.OpenPositionPPL;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Message {
    private Map<String, OpenPositionPPL> openPositions;

    public Message() {
        this.openPositions = new ConcurrentHashMap<>();
    }

    public Message(Map<String, OpenPositionPPL> openPositions) {
        this.openPositions = openPositions;
    }

    public Map<String, OpenPositionPPL> getOpenPositions() {
        return openPositions;
    }

    public void setOpenPositions(Map<String, OpenPositionPPL> openPositions) {
        this.openPositions = openPositions;
    }
}
