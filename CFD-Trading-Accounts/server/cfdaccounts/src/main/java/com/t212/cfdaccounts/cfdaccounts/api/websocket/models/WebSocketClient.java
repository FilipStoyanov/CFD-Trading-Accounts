package com.t212.cfdaccounts.cfdaccounts.api.websocket.models;

import com.t212.cfdaccounts.cfdaccounts.core.models.OpenPositionPPL;
import com.t212.cfdaccounts.cfdaccounts.repositories.models.AccountPositionDAO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketClient {
    private Map<String, OpenPositionPPL> openPositions;
    private Map<String, AccountPositionDAO> positions;
    private BigDecimal lockedCash;
    private BigDecimal result;

    public WebSocketClient() {
        this.openPositions = new ConcurrentHashMap<>();
        this.positions = new ConcurrentHashMap<>();
    }

    public WebSocketClient(Map<String, OpenPositionPPL> openPositions, Map<String, AccountPositionDAO> positions, BigDecimal lockedCash, BigDecimal result) {
        this.openPositions = openPositions;
        this.positions = positions;
        this.lockedCash = lockedCash;
        this.result = result;
    }

    public Map<String, OpenPositionPPL> getOpenPositions() {
        return openPositions;
    }

    public Map<String, AccountPositionDAO> getPositions() {
        return positions;
    }

    public void setPositions(List<AccountPositionDAO> positions) {
        for (AccountPositionDAO p : positions) {
            this.positions.put(p.ticker + "_" + p.type, p);
        }
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

    public void setPositions(Map<String, AccountPositionDAO> positions) {
        this.positions = positions;
    }

    public void setOpenPositions(Map<String, OpenPositionPPL> openPositions) {
        this.openPositions = openPositions;
    }

    @Override
    public String toString() {
        return "WebSocketClient[" +
                "openPositions=" + openPositions + ", " +
                "positions=" + positions;

    }
}
