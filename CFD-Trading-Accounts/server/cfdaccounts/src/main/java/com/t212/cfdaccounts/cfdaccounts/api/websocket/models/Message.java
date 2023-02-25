package com.t212.cfdaccounts.cfdaccounts.api.websocket.models;

import com.t212.cfdaccounts.cfdaccounts.core.models.OpenPositionPPL;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Message {
    private Map<String, OpenPositionPPL> openPositions;
    private BigDecimal freeCash;
    private BigDecimal lockedCash;

    private BigDecimal status;

    public Message() {
        this.openPositions = new ConcurrentHashMap<>();
    }

    public Message(Map<String, OpenPositionPPL> openPositions, BigDecimal freeCash, BigDecimal lockedCash, BigDecimal status) {
        this.openPositions = openPositions;
        this.freeCash = freeCash;
        this.lockedCash = lockedCash;
        this.status = status;
    }

    public Map<String, OpenPositionPPL> getOpenPositions() {
        return openPositions;
    }

    public void setOpenPositions(Map<String, OpenPositionPPL> openPositions) {
        this.openPositions = openPositions;
    }

    public BigDecimal getFreeCash() {
        return freeCash;
    }

    public void setFreeCash(BigDecimal freeCash) {
        this.freeCash = freeCash;
    }

    public BigDecimal getLockedCash() {
        return lockedCash;
    }

    public void setLockedCash(BigDecimal lockedCash) {
        this.lockedCash = lockedCash;
    }

    public BigDecimal getStatus() {
        return status;
    }

    public void setStatus(BigDecimal status) {
        this.status = status;
    }
}
