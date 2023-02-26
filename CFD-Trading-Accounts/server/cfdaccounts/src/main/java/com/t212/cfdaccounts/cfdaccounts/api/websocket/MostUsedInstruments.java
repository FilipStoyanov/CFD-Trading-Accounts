package com.t212.cfdaccounts.cfdaccounts.api.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.t212.cfdaccounts.cfdaccounts.events.StockPriceUpdateEvent;
import com.t212.cfdaccounts.cfdaccounts.serviceclient.InstrumentClient;
import com.t212.cfdaccounts.cfdaccounts.serviceclient.models.InstrumentWithPrice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MostUsedInstruments {
    private Map<String, InstrumentWithPrice> mostUsedInstruments;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    public final InstrumentClient instrumentClient;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public MostUsedInstruments(ObjectMapper objectMapper, RestTemplate restTemplate, InstrumentClient instrumentClient) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        this.instrumentClient = instrumentClient;
        mostUsedInstruments = new ConcurrentHashMap<>();
        initializeMostUsedInstruments();
    }

    public void initializeMostUsedInstruments() {
        try {
            mostUsedInstruments = instrumentClient.getMostUsedInstruments();
        } catch (JsonProcessingException e) {
            //
        }
    }

    public Map<String, InstrumentWithPrice> getMostUsedInstruments() {
        return mostUsedInstruments;
    }

    @KafkaListener(
            topics = "quotes.raw.cfd",
            groupId = "cfd_quotes_most_used",
            containerFactory = "StockPricesUpdatedContainerFactory")
    void listenForPrices(StockPriceUpdateEvent data) {
        if (mostUsedInstruments.containsKey(data.ticker())) {
            InstrumentWithPrice ins = mostUsedInstruments.get(data.ticker());
            ins.setBuy(data.ask());
            ins.setSell(data.bid());
            mostUsedInstruments.put(data.ticker(), ins);
        }
    }

    @Scheduled(fixedRate = 5000)
    void sendMessageToChannelForMostUsedInstruments() {
        messagingTemplate.convertAndSend("/cfd/stocks/most-used", mostUsedInstruments);
    }
}
