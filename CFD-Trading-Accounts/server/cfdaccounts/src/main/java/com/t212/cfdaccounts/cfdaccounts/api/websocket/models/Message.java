package com.t212.cfdaccounts.cfdaccounts.api.websocket.models;

import com.t212.cfdaccounts.cfdaccounts.core.models.OpenPositionPPL;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Message {
    private Map<String, OpenPositionPPL> openPositions;
    private BigDecimal lockedCash;
    private BigDecimal result;

    public Message() {
        this.openPositions = new ConcurrentHashMap<>();
    }

    public Message(Map<String, OpenPositionPPL> openPositions, BigDecimal lockedCash, BigDecimal result) {
        this.openPositions = openPositions;
        this.lockedCash = lockedCash;
        this.result = result;
    }

    public Map<String, OpenPositionPPL> getOpenPositions() {
        return openPositions;
    }

    public BigDecimal getLockedCash() {
        return lockedCash;
    }

    public void setLockedCash(BigDecimal lockedCash) {
        this.lockedCash = lockedCash;
    }

    public BigDecimal getResult() {
        return result;
    }

    public void setResult(BigDecimal result) {
        this.result = result;
    }

    public void setOpenPositions(Map<String, OpenPositionPPL> openPositions) {
        this.openPositions = openPositions;
    }
}
