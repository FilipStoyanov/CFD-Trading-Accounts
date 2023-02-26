package com.t212.cfdaccounts.cfdaccounts.core;

import com.t212.cfdaccounts.cfdaccounts.events.PositionUpdateEvent;
import com.t212.cfdaccounts.cfdaccounts.repositories.models.AccountPositionDAO;
import com.t212.cfdaccounts.cfdaccounts.serviceclient.PositionsClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PositionsListener {
    private Map<Long, Map<String, AccountPositionDAO>> userPositions;

    public PositionsListener(PositionsClient positionsClient) {
        this.userPositions = new ConcurrentHashMap<>();
    }

    @KafkaListener(
            topics = "positions.open.cfd",
            groupId = "cfd_positions_updates",
            containerFactory = "PositionsUpdatedContainerFactory")
    void listenForOpeningPosition(PositionUpdateEvent data) {
        if (data != null) {
            userPositions.putIfAbsent(data.userId(), new ConcurrentHashMap<>());
            Map<String, AccountPositionDAO> positions = userPositions.get(data.userId());
            positions.put(data.ticker(), new AccountPositionDAO(data.userId(), data.ticker(), data.quantity(), data.positionType(), data.buyPrice(), data.sellPrice(), new Date(data.timestamp()), new Date(data.timestamp())));
        }
    }

    @KafkaListener(
            topics = "positions.close.cfd",
            groupId = "cfd_positions_updates",
            containerFactory = "PositionsUpdatedContainerFactory")
    void listenForClosingPosition(PositionUpdateEvent data) {
        if (data != null) {
            if (userPositions.containsKey(data.userId())) {
                if (userPositions.get(data.userId()).containsKey(data.ticker())) {
                    userPositions.get(data.userId()).remove(data.ticker());
                }
            }
        }
    }

    public Map<String, AccountPositionDAO> getUserPositions(long userId) {
        return userPositions.get(userId);
    }

    public void setUserPositions(Map<String, AccountPositionDAO> positions, long userId) {
        userPositions.put(userId, positions);
    }
}