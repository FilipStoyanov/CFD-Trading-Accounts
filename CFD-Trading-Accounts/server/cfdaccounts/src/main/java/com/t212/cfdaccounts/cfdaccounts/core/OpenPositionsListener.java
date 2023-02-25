package com.t212.cfdaccounts.cfdaccounts.core;

import com.t212.cfdaccounts.cfdaccounts.events.PositionsUpdaterEvent;
import com.t212.cfdaccounts.cfdaccounts.repositories.models.AccountPositionDAO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OpenPositionsListener {
    private Map<Long, PositionsUpdaterEvent> positionsUpdaterEvents;
    private final StockPricesListener stockPricesListener;

    private Map<Long, List<AccountPositionDAO>> userPositions;


    public OpenPositionsListener(StockPricesListener stockPricesListener) {
        positionsUpdaterEvents = new ConcurrentHashMap<>();
        this.stockPricesListener = stockPricesListener;
    }

    @KafkaListener(
            topics = "positions.cfd",
            groupId = "cfd_positions_updates",
            containerFactory = "PositionsUpdatedContainerFactory")
    void listenForPositionsUpdate(PositionsUpdaterEvent data) {
        positionsUpdaterEvents.put(data.userId, data);
    }

    public Map<Long, PositionsUpdaterEvent> getPositionsUpdaterEvents() {
        return positionsUpdaterEvents;
    }

    public PositionsUpdaterEvent getPositionsUpdaterEventsForUser(long userId) {
        return positionsUpdaterEvents.get(userId);
    }
}