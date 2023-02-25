package com.t212.cfdaccounts.cfdaccounts.api.websocket.models;

import com.t212.cfdaccounts.cfdaccounts.core.models.OpenPositionPPL;
import com.t212.cfdaccounts.cfdaccounts.repositories.models.AccountPositionDAO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketClient {
    private Map<String, OpenPositionPPL> openPositions;
    private BigDecimal freeCash;
    private BigDecimal lockedCash;

    private BigDecimal status;
    private Map<String, AccountPositionDAO> positions;

    public WebSocketClient() {
        this.openPositions = new ConcurrentHashMap<>();
        this.positions = new ConcurrentHashMap<>();
    }

    public WebSocketClient(Map<String, InstrumentToPrice> instruments, Map<String, OpenPositionPPL> openPositions, BigDecimal freeCash, BigDecimal lockedCash, BigDecimal status, Map<String, AccountPositionDAO> positions) {
        this.openPositions = openPositions;
        this.freeCash = freeCash;
        this.lockedCash = lockedCash;
        this.status = status;
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

    @Override
    public String toString() {
        return "Message[" +
                "openPositions=" + openPositions + ", " +
                "freeCash=" + freeCash + ", " +
                "lockedCash=" + lockedCash + ", " +
                "status=" + status + "]";
    }
}
