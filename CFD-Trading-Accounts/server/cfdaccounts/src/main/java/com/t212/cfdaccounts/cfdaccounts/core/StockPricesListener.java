package com.t212.cfdaccounts.cfdaccounts.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.t212.cfdaccounts.cfdaccounts.events.StockPriceUpdateEvent;
import com.t212.cfdaccounts.cfdaccounts.serviceclient.InstrumentClient;
import com.t212.cfdaccounts.cfdaccounts.serviceclient.models.InstrumentWithPrice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StockPricesListener {
    private final InstrumentClient instrumentClient;
    private final RedisTemplate<String, StockPriceUpdateEvent> redisTemplate;
    private Map<String, InstrumentWithPrice> instrumentPrices;

    public StockPricesListener(RedisTemplate<String, StockPriceUpdateEvent> redisTemplate, InstrumentClient instrumentClient) {
        this.instrumentClient = instrumentClient;
        this.redisTemplate = redisTemplate;
        this.instrumentPrices = new ConcurrentHashMap<>();
        try {
            instrumentPrices = instrumentClient.getInstruments();
        } catch (JsonProcessingException e) {
            //todo: handle this exception
        }
    }

    @KafkaListener(
            topics = "quotes.raw.cfd",
            groupId = "cfd_stock_prices",
            containerFactory = "StockPricesUpdatedContainerFactory")
    void listenForStockPrices(StockPriceUpdateEvent data) {
        InstrumentWithPrice currentInstrument = instrumentPrices.get(data.ticker);
        currentInstrument.buy = data.ask;
        currentInstrument.sell = data.bid;
        if (data != null && data.ticker != null) {
            instrumentPrices.put(data.ticker, currentInstrument);
            redisTemplate.opsForValue().set(data.ticker, data);
        }
    }

    public Map<String, InstrumentWithPrice> getInstrumentPrices() {
        return instrumentPrices;
    }
}
