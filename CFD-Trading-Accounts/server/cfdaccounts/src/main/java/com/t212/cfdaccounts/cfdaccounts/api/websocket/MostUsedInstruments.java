package com.t212.cfdaccounts.cfdaccounts.api.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.t212.cfdaccounts.cfdaccounts.api.websocket.models.ConnectionError;
import com.t212.cfdaccounts.cfdaccounts.events.StockPriceUpdateEvent;
import com.t212.cfdaccounts.cfdaccounts.serviceclient.InstrumentClient;
import com.t212.cfdaccounts.cfdaccounts.serviceclient.models.InstrumentWithPrice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MostUsedInstruments {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    private List<InstrumentWithPrice> listOfInstruments;
    private Map<String, InstrumentWithPrice> instruments;
    private Map<Long, String> userGraphics;
    private Map<Long, Integer> usersPage;
    public final InstrumentClient instrumentClient;
    private static final Logger LOGGER = LoggerFactory.getLogger(MostUsedInstruments.class);
    private final RestTemplate restTemplate;
    private final static int ITEMS_ON_PAGE = 4;

    private boolean connectionError;

    public MostUsedInstruments(RestTemplate restTemplate, InstrumentClient instrumentClient) {
        this.restTemplate = restTemplate;
        this.instrumentClient = instrumentClient;
        this.userGraphics = new ConcurrentHashMap<>();
        this.usersPage = new ConcurrentHashMap<>();
        instruments = new ConcurrentHashMap<>();
        initializeMostUsedInstruments();
    }

    public void initializeMostUsedInstruments() {
        try {
            listOfInstruments = instrumentClient.getAllInstruments();
            for (InstrumentWithPrice i : listOfInstruments) {
                instruments.put(i.ticker, i);
            }
            connectionError = false;
        } catch (JsonProcessingException e) {
            LOGGER.warn("Exception occurred:", e);
        } catch (ResourceAccessException e) {
            LOGGER.warn("Exception occurred:", e);
            connectionError = true;
        }
    }

    public void addPageToUser(long userId, Integer page) {
        usersPage.put(userId, page);
    }

    public void addGraphicForUser(long userId, String tickerName) {
        userGraphics.put(userId, tickerName);
    }

    public Map<String, InstrumentWithPrice> getInstruments() {
        return instruments;
    }

    @KafkaListener(
            topics = "quotes.raw.cfd",
            groupId = "cfd_quotes_most_used-#{ T(java.util.UUID).randomUUID().toString() }",
            containerFactory = "StockPricesUpdatedContainerFactory")
    void listenForPrices(StockPriceUpdateEvent data) {
        if (instruments.containsKey(data.ticker())) {
            InstrumentWithPrice ins = instruments.get(data.ticker());
            ins.setBuy(data.ask());
            ins.setSell(data.bid());
            instruments.put(data.ticker(), ins);
        }
    }

    @Scheduled(fixedRate = 5000)
    void sendMessageToChannelForMostUsedInstruments() {
        if (connectionError) {
            messagingTemplate.convertAndSend("/cfd/errors", new ConnectionError("error", "An error has occurred"));
            initializeMostUsedInstruments();
        } else {
            for (Map.Entry<Long, Integer> user : usersPage.entrySet()) {
                int lastElement = user.getValue() + ITEMS_ON_PAGE < instruments.size() ? user.getValue() + ITEMS_ON_PAGE : instruments.size() + 1;
                messagingTemplate.convertAndSend("/cfd/users/" + user.getKey() + "/instruments/",
                        listOfInstruments.stream().skip(user.getValue()).limit(lastElement - user.getValue() - 1));
                messagingTemplate.convertAndSend("/cfd/users/" + user.getKey() + "/graphic",
                        listOfInstruments.stream().filter(instrument -> instrument.ticker.equals(userGraphics.get(user.getKey()))).findFirst().get());
            }
        }
    }
}
