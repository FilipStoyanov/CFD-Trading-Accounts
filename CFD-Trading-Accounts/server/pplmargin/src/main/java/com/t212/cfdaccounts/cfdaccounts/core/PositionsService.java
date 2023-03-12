package com.t212.cfdaccounts.cfdaccounts.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.t212.cfdaccounts.cfdaccounts.api.websocket.models.ConnectionError;
import com.t212.cfdaccounts.cfdaccounts.events.PositionUpdateEvent;
import com.t212.cfdaccounts.cfdaccounts.repositories.models.AccountPositionDAO;
import com.t212.cfdaccounts.cfdaccounts.serviceclient.PositionsClient;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PositionsService {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    private Map<Long, Map<String, AccountPositionDAO>> userPositions;
    private final PositionsClient positionClient;

    public PositionsService(PositionsClient positionsClient) {
        this.userPositions = new ConcurrentHashMap<>();
        this.positionClient = positionsClient;
    }

    @RetryableTopic(
            attempts = "3",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            include = {Exception.class}
    )
    @KafkaListener(
            topics = "positions.open.cfd",
            groupId = "cfd_positions_open-#{ T(java.util.UUID).randomUUID().toString() }",
            containerFactory = "PositionsUpdatedContainerFactory",
            concurrency = "1")
    void listenForOpeningPosition(ConsumerRecord<String, PositionUpdateEvent> data, Acknowledgment acknowledgment, Consumer<?, ?> consumer) {
        if (data != null) {
            userPositions.putIfAbsent(data.value().userId(), new ConcurrentHashMap<>());
            Map<String, AccountPositionDAO> positions = userPositions.get(data.value().userId());
            positions.put(data.value().ticker() + "_" + data.value().positionType(), new AccountPositionDAO(data.value().userId(), data.value().ticker(), data.value().quantity(), data.value().positionType(), data.value().buyPrice(), data.value().sellPrice(), new Date(data.timestamp()), new Date(data.timestamp())));
            acknowledgment.acknowledge();
        }
    }

    @RetryableTopic(
            attempts = "3",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            include = {Exception.class}
    )
    @KafkaListener(
            topics = "positions.close.cfd",
            groupId = "cfd_positions_close-#{ T(java.util.UUID).randomUUID().toString() }",
            containerFactory = "PositionsUpdatedContainerFactory",
            concurrency =  "6")
    void listenForClosingPosition(ConsumerRecord<String, PositionUpdateEvent> data, Acknowledgment acknowledgment, Consumer<?, ?> consumer) {
        if (data != null) {
            if (userPositions.containsKey(data.value().userId())) {
                if (userPositions.get(data.value().userId()).containsKey(data.value().ticker() + "_" + data.value().positionType())) {
                    userPositions.get(data.value().userId()).remove(data.value().ticker() + "_" + data.value().positionType());
                }
            }
            acknowledgment.acknowledge();
        }
    }

    @DltHandler
    public void handleDlt(ConsumerRecord<String, PositionUpdateEvent> record, Acknowledgment
            acknowledgment, Consumer<?, ?> consumer) {
        if (record.value() != null && record.value().userId() > 0) {
            try {
                List<AccountPositionDAO> openPositionsForCurrentUser = positionClient.getOpenPositions(record.value().userId());
                Map<String, AccountPositionDAO> mapOfOpenPositions = new ConcurrentHashMap<>();
                for (AccountPositionDAO p : openPositionsForCurrentUser) {
                    mapOfOpenPositions.put(p.ticker(), p);
                }
                userPositions.put(record.value().userId(), mapOfOpenPositions);
            } catch (JsonProcessingException e) {
                consumer.seek(new TopicPartition(record.topic(), record.partition()), record.offset());
            } catch (ResourceAccessException e) {
                messagingTemplate.convertAndSend("/cfd/errors", new ConnectionError("error", "An error has occurred"));
            }

        }
        acknowledgment.acknowledge();
    }

    public Map<String, AccountPositionDAO> getUserPositions(long userId) {
        return userPositions.get(userId);
    }

    public void setUserPositions(Map<String, AccountPositionDAO> positions, long userId) {
        userPositions.put(userId, positions);
    }
}