package com.t212.tickers.producer.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.t212.tickers.producer.gateways.KafkaGateway;
import com.t212.tickers.producer.lib.events.StockPriceUpdateEvent;
import com.t212.tickers.producer.serviceclient.InstrumentClient;
import com.t212.tickers.producer.serviceclient.models.Instrument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class QuotesGenerator {
    @Autowired
    private KafkaGateway kafkaGateway;
    private final InstrumentClient instrumentClient;
    private List<Instrument> instruments;
    public static final int MESSAGES_PER_MILLISECONDS = 10;
    private final ExecutorService executorService;

    public QuotesGenerator(KafkaGateway kafkaGateway, InstrumentClient instrumentClient) {
        this.instrumentClient = instrumentClient;
        this.kafkaGateway = kafkaGateway;
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
}
