package com.t212.cfdaccounts.cfdaccounts.bin.pricesgenerator;

import com.t212.cfdaccounts.cfdaccounts.events.StockPriceUpdateEvent;
import com.t212.cfdaccounts.cfdaccounts.gateways.KafkaGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Configuration
public class StockPricesFeed {
    @Autowired
    private KafkaGateway kafkaGateway;
    private List<String> tickers;

    public StockPricesFeed(KafkaGateway kafkaGateway) {
        kafkaGateway = kafkaGateway;
        tickers = new ArrayList<>(Arrays.asList("Crude-Oil-01March23", "Gold", "EUR/USD", "USD/JPY", "Tesla", "Apple", "Meta Platforms", "Netflix", "Amazon", "Alphabet(Class C)",
                "Virgin Galactic", "Smile Direct Club", "Airbnb Inc"));
    }

    public void sendMessages() {
        Integer randomIndex = new Random().nextInt(0, tickers.size());
        BigDecimal randomBuyPrice = BigDecimal.valueOf(new Random().nextDouble(0, 200));
        BigDecimal randomSellPrice = BigDecimal.valueOf(new Random().nextDouble(0, 200));
        StockPriceUpdateEvent sEvent = new StockPriceUpdateEvent(tickers.get(randomIndex), randomBuyPrice, randomSellPrice, System.currentTimeMillis());
        kafkaGateway.sendStockPriceUpdateEvent(sEvent);
    }

    @Scheduled(fixedRate = 1)
    public void publicStockPricesEvent() {
        sendMessages();
    }
}
