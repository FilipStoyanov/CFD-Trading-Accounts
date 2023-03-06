package com.t212.cfdaccounts.cfdaccounts.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.t212.cfdaccounts.cfdaccounts.events.StockPriceUpdateEvent;
import com.t212.cfdaccounts.cfdaccounts.serviceclient.InstrumentClient;
import com.t212.cfdaccounts.cfdaccounts.serviceclient.models.InstrumentWithPrice;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StockPricesListener {
    private final InstrumentClient instrumentClient;
    private static final Logger LOGGER = LoggerFactory.getLogger(StockPricesListener.class);
    private Map<String, InstrumentWithPrice> instrumentPrices;
    private boolean hasConnectionError;
    @Value("${kafka.stock-prices-partition}")
    private static String topicPartition;

    private Environment environment;

    public StockPricesListener(InstrumentClient instrumentClient, Environment environment) {
        this.instrumentClient = instrumentClient;
        this.instrumentPrices = new ConcurrentHashMap<>();
        this.environment = environment;
        loadInstrumentsWithPrices();
    }

    @KafkaListener(
            topics = "quotes.raw.cfd",
            groupId = "cfd_stock_prices",
            containerFactory = "StockPricesUpdatedContainerFactory",
            topicPartitions = {@TopicPartition(topic = "quotes.raw.cfd", partitions = "${kafka.stock-prices-partition}")}
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
