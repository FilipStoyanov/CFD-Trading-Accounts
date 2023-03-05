package com.t212.cfdaccounts.cfdaccounts.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.t212.cfdaccounts.cfdaccounts.events.StockPriceUpdateEvent;
import com.t212.cfdaccounts.cfdaccounts.serviceclient.InstrumentClient;
import com.t212.cfdaccounts.cfdaccounts.serviceclient.models.InstrumentWithPrice;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StockPricesListener {
    private final InstrumentClient instrumentClient;
    private static final Logger LOGGER = LoggerFactory.getLogger(StockPricesListener.class);
    private Map<String, InstrumentWithPrice> instrumentPrices;
    private boolean hasConnectionError;

    public StockPricesListener(InstrumentClient instrumentClient) {
        this.instrumentClient = instrumentClient;
        this.instrumentPrices = new ConcurrentHashMap<>();
        loadInstrumentsWithPrices();
    }

    @KafkaListener(
            topics = "quotes.raw.cfd",
            groupId = "cfd_stock_prices-#{ T(java.util.UUID).randomUUID().toString() }",
            containerFactory = "StockPricesUpdatedContainerFactory",
            concurrency = "6"
    )
    void listenForStockPrices(ConsumerRecord<String, StockPriceUpdateEvent> data, Acknowledgment acknowledgment, Consumer<?, ?> consumer) {
        if (data != null && instrumentPrices != null) {
            InstrumentWithPrice currentInstrument = instrumentPrices.get(data.value().ticker());
            StockPriceUpdateEvent event = data.value();
            currentInstrument.buy = event.ask();
            currentInstrument.sell = event.bid();
            instrumentPrices.put(event.ticker(), currentInstrument);
            acknowledgment.acknowledge();
        }
    }

    public Map<String, InstrumentWithPrice> getInstrumentPrices() {
        return instrumentPrices;
    }

    public boolean hasConnectionError() {
        return hasConnectionError;
    }

    public void loadInstrumentsWithPrices() {
        try {
            instrumentPrices = instrumentClient.getInstruments();
            hasConnectionError = false;
        } catch (JsonProcessingException e) {
            LOGGER.warn("Exception occurred:", e);
        } catch (ResourceAccessException e) {
            hasConnectionError = true;
        }
    }
}
