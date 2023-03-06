package com.t212.tickers.producer.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.t212.tickers.producer.core.models.InstrumentUpdater;
import com.t212.tickers.producer.gateways.KafkaGateway;
import com.t212.tickers.producer.lib.events.StockPriceUpdateEvent;
import com.t212.tickers.producer.repositories.InstrumentRepository;
import com.t212.tickers.producer.serviceclient.InstrumentClient;
import com.t212.tickers.producer.serviceclient.models.Instrument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Configuration
public class QuotesGenerator {
    @Autowired
    private KafkaGateway kafkaGateway;

    private final InstrumentRepository instrumentRepository;
    private final InstrumentClient instrumentClient;
    private List<Instrument> instruments;
    private Map<String, InstrumentUpdater> lastPricesForInstruments;
    public static final int MESSAGES_PER_MILLISECONDS = 10;
    private final ExecutorService executorService;

    private final static int BATCH_SIZE = 100;

    public QuotesGenerator(KafkaGateway kafkaGateway, InstrumentClient instrumentClient, InstrumentRepository instrumentRepository) {
        this.instrumentClient = instrumentClient;
        this.instrumentRepository = instrumentRepository;
        this.kafkaGateway = kafkaGateway;
        this.lastPricesForInstruments = new ConcurrentHashMap<>();
        executorService = Executors.newFixedThreadPool(6);
        loadInstruments();
    }

    private void loadInstruments() {
        this.instruments = new ArrayList<>();
        try {
            instruments = instrumentClient.getInstruments();
        } catch (JsonProcessingException e) {
            //
        }
    }

    public void sendMessages() {
        Integer randomIndex = new Random().nextInt(0, Integer.MAX_VALUE) % instruments.size();
        BigDecimal randomBuyPrice = BigDecimal.valueOf(new Random().nextDouble(0, 200));
        BigDecimal randomSellPrice = BigDecimal.valueOf(new Random().nextDouble(0, 200));
        Instrument instrument = instruments.get(randomIndex);
        StockPriceUpdateEvent sEvent = new StockPriceUpdateEvent(instrument.ticker(), randomBuyPrice, randomSellPrice, System.currentTimeMillis());
        lastPricesForInstruments.put(instrument.ticker(), new InstrumentUpdater(instrument.ticker(), randomBuyPrice, randomSellPrice));
        String key = instrument.type() + "-" + instrument.ticker();
        kafkaGateway.sendStockPriceUpdateEvent(key, sEvent);
    }

    @Scheduled(fixedRate = 1)
    public void publicStockPricesEvent() {
//        for (int i = 0; i < MESSAGES_PER_MILLISECONDS; ++i) {
//            executorService.submit(() -> {
        sendMessages();
//            });
//        }
    }

    @Scheduled(fixedRate = 5000)
    public void updateInstrumentPrices() {
        List<InstrumentUpdater> batch = lastPricesForInstruments.values().stream().collect(Collectors.toList());
        int numberOfBatches = batch.size() / BATCH_SIZE;
        for (int i = 0; i < batch.size(); i = i + BATCH_SIZE) {
            if (i + BATCH_SIZE >= batch.size()) {
                instrumentRepository.batchUpdate(batch.subList(i, batch.size()));
                break;
            }
            instrumentRepository.batchUpdate(batch.subList(i, i + BATCH_SIZE));
        }
    }
}
