package com.t212.tickers.producer.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.t212.tickers.producer.gateways.KafkaGateway;
import com.t212.tickers.producer.lib.events.StockPriceUpdateEvent;
import com.t212.tickers.producer.serviceclient.InstrumentClient;
import com.t212.tickers.producer.serviceclient.models.Instrument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.util.*;

@Configuration
public class QuotesGenerator {
    @Autowired
    private KafkaGateway kafkaGateway;
    @Value("${kafka.stock-prices-topic}")
    private String kafkaTopic;
    private final InstrumentClient instrumentClient;
    private List<Instrument> instruments;

    public QuotesGenerator(KafkaGateway kafkaGateway, InstrumentClient instrumentClient) {
        this.instrumentClient = instrumentClient;
        this.kafkaGateway = kafkaGateway;
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
        BigDecimal randomBuyPrice = BigDecimal.valueOf(new Random().nextDouble(188, 193));
        BigDecimal randomSellPrice = BigDecimal.valueOf(new Random().nextDouble(188, 193));
        Instrument instrument = instruments.get(randomIndex);
        StockPriceUpdateEvent sEvent = new StockPriceUpdateEvent(instrument.ticker(), randomBuyPrice, randomSellPrice, System.currentTimeMillis());
        kafkaGateway.sendStockPriceUpdateEvent(kafkaTopic, instrument.ticker(), sEvent);
    }

    @Scheduled(fixedRate = 1)
    public void publicStockPricesEvent() {
        sendMessages();
    }
}