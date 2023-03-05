package com.t212.cfdaccounts.cfdaccounts.api.websocket.models;

public class ClientGraphicMessage {
    private String instrumentName;

    public ClientGraphicMessage() {
    }

    public ClientGraphicMessage(String instrumentName) {
        this.instrumentName = instrumentName;
    }

    public String getInstrumentName() {
        return instrumentName;
    }

    public void setInstrumentName(String instrumentName) {
        this.instrumentName = instrumentName;
    }
}
