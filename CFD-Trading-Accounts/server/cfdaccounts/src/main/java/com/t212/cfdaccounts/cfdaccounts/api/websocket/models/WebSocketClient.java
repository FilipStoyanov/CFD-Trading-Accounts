package com.t212.cfdaccounts.cfdaccounts.api.websocket.models;

import com.t212.cfdaccounts.cfdaccounts.core.models.OpenPositionPPL;
import com.t212.cfdaccounts.cfdaccounts.repositories.models.AccountPositionDAO;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketClient {
    private Map<String, OpenPositionPPL> openPositions;
    private Map<String, AccountPositionDAO> positions;

    public WebSocketClient() {
        this.openPositions = new ConcurrentHashMap<>();
        this.positions = new ConcurrentHashMap<>();
    }

    public WebSocketClient(Map<String, OpenPositionPPL> openPositions, Map<String, AccountPositionDAO> positions) {
        this.openPositions = openPositions;
        this.positions = positions;
    }

    public Map<String, OpenPositionPPL> getOpenPositions() {
        return openPositions;
    }

    public Map<String, AccountPositionDAO> getPositions() {
        return positions;
    }

    public void setPositions(List<AccountPositionDAO> positions) {
        for (AccountPositionDAO p : positions) {
            this.positions.put(p.ticker, p);
        }
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
