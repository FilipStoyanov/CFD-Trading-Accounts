package com.t212.cfdaccounts.cfdaccounts.api.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.t212.cfdaccounts.cfdaccounts.api.websocket.models.ConnectionError;
import com.t212.cfdaccounts.cfdaccounts.api.websocket.models.WebSocketClient;
import com.t212.cfdaccounts.cfdaccounts.api.websocket.services.WebSocketService;
import com.t212.cfdaccounts.cfdaccounts.core.Mappers;
import com.t212.cfdaccounts.cfdaccounts.core.PositionsListener;
import com.t212.cfdaccounts.cfdaccounts.core.StockPricesListener;
import com.t212.cfdaccounts.cfdaccounts.core.models.OpenPositionPPL;
import com.t212.cfdaccounts.cfdaccounts.repositories.models.AccountPositionDAO;
import com.t212.cfdaccounts.cfdaccounts.serviceclient.PositionsClient;
import com.t212.cfdaccounts.cfdaccounts.serviceclient.models.InstrumentWithPrice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class PPLAndMarginHandler {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    private final StockPricesListener stockPricesListener;
    @Autowired
    private PositionsListener positionsListener;
    private final WebSocketService websocketService;

    private static final Logger LOGGER = LoggerFactory.getLogger(PPLAndMarginHandler.class);
    private final PositionsClient positionClient;
    private Map<Long, WebSocketClient> messagesForUsers;

    private final ExecutorService executorService;

    public PPLAndMarginHandler(WebSocketService websocketService, PositionsClient positionClient, StockPricesListener stockPricesListener) {
        this.websocketService = websocketService;
        this.stockPricesListener = stockPricesListener;
        this.messagesForUsers = new ConcurrentHashMap<>();
        this.positionClient = positionClient;
        executorService = Executors.newFixedThreadPool(6);
    }

    private Map<String, OpenPositionPPL> calculateOpenPositionsMarginAndPPL(Map<String, AccountPositionDAO> mapOfOpenPositions) {
        Map<String, OpenPositionPPL> openPositionPPL = new ConcurrentHashMap<>();
        BigDecimal price, currentPrice, spread, result, margin;
        InstrumentWithPrice currentInstrument;
        for (Map.Entry<String, AccountPositionDAO> position : mapOfOpenPositions.entrySet()) {
            if (stockPricesListener.getInstrumentPrices().containsKey(position.getValue().ticker())) {
                currentInstrument = stockPricesListener.getInstrumentPrices().get(position.getValue().ticker());
                price = position.getValue().type().equals("LONG") ? position.getValue().buyPrice() : position.getValue().sellPrice();
                currentPrice = position.getValue().type().equals("LONG") ? currentInstrument.buy : currentInstrument.sell;
                // spread = (buyPrice - sellPrice) * quantity
                spread = (position.getValue().buyPrice().subtract(position.getValue().sellPrice())).multiply(position.getValue().quantity());
                // result = (currentPrice - price) * quantity + spread
                result = (currentPrice.subtract(price)).multiply(position.getValue().quantity()).add(spread);
                //margin = ask/bid price * leverage
                margin = position.getValue().quantity().multiply(currentPrice.multiply(currentInstrument.leverage));
                openPositionPPL.put(position.getValue().ticker() + "_" + position.getValue().type(), new OpenPositionPPL(position.getValue().ticker(), position.getValue().quantity(), position.getValue().type(), price, currentPrice, margin, result));
            }
        }
        return openPositionPPL;
    }

    private BigDecimal calculateStatus(BigDecimal lockedCash, BigDecimal totalFunds) {
        BigDecimal status;
        BigDecimal div = lockedCash.add(totalFunds);
        BigDecimal fundsProportion = totalFunds.divide(div, 10, RoundingMode.FLOOR);
        if (fundsProportion.compareTo(BigDecimal.valueOf(0.5)) < 0) {
            status = fundsProportion.multiply(BigDecimal.valueOf(50));
        } else {
            status = fundsProportion.multiply(BigDecimal.valueOf(100));
        }
        return status;
    }


    private Map<String, BigDecimal> calculateCashAndStatus(Map<String, OpenPositionPPL> openPositions) {
        Map<String, BigDecimal> balance = new ConcurrentHashMap<>();
        BigDecimal lockedCash = new BigDecimal(0);
        BigDecimal result = new BigDecimal(0);
        for (Map.Entry<String, OpenPositionPPL> position : openPositions.entrySet()) {
            lockedCash = lockedCash.add(position.getValue().margin());
            result = result.add(position.getValue().result());
        }
        balance.put("lockedCash", lockedCash);
        balance.put("result", result);
        return balance;
    }


    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        String subscribedUrl = (String) event.getMessage().getHeaders().get("simpDestination");
        if (subscribedUrl.startsWith("/cfd/quotes")) {
            List<String> urlParts = Arrays.stream(subscribedUrl.split("/")).toList();
            long userId = Long.parseLong(urlParts.get(urlParts.size() - 1));
            WebSocketClient message = new WebSocketClient();
            try {
                List<AccountPositionDAO> openPositionsForCurrentUser = positionClient.getOpenPositions(userId);
                message.setPositions(openPositionsForCurrentUser);
                Map<String, OpenPositionPPL> openPositionsWithPPLAndMargin = calculateOpenPositionsMarginAndPPL(message.getPositions());
                message.setOpenPositions(openPositionsWithPPLAndMargin);
                messagesForUsers.put(userId, message);
                websocketService.addConnection(userId);
            } catch (JsonProcessingException e) {
                LOGGER.warn("Exception occurred:", e);
            } catch (ResourceAccessException e) {
                LOGGER.warn("Exception occurred:", e);
                messagingTemplate.convertAndSend("/cfd/errors", new ConnectionError("error", "An error has occurred"));
            }
        }
    }

    private Map<String, AccountPositionDAO> updateUserPositions(long userId) {
        return positionsListener.getUserPositions(userId);
    }

    @EventListener
    public void handleUnsubscribeEvent(SessionUnsubscribeEvent event) {
        String subscribedUrl = (String) event.getMessage().getHeaders().get("simpDestination");
        if (subscribedUrl.startsWith("/cfd/quotes")) {
            List<String> urlParts = Arrays.stream(subscribedUrl.split("/")).toList();
            int userId = Integer.parseInt(urlParts.get(urlParts.size() - 1));
            messagesForUsers.remove(userId);
            websocketService.removeConnection(userId);
        }
    }

    private void sendMessageToUser(long userId) {
        WebSocketClient webSocketClient = new WebSocketClient();
        Map<String, AccountPositionDAO> userOpenPositions = updateUserPositions(userId);
        if (userOpenPositions == null) {
            userOpenPositions = messagesForUsers.get(userId).getPositions();
            positionsListener.setUserPositions(userOpenPositions, userId);
        }
        webSocketClient.setPositions(userOpenPositions);
        Map<String, OpenPositionPPL> openPositionsWithPPLAndMargin = calculateOpenPositionsMarginAndPPL(userOpenPositions);
        webSocketClient.setOpenPositions(openPositionsWithPPLAndMargin);
        Map<String, BigDecimal> accountBalance = calculateCashAndStatus(webSocketClient.getOpenPositions());
        webSocketClient.setLockedCash(accountBalance.get("lockedCash"));
        webSocketClient.setResult(accountBalance.get("result"));
        messagesForUsers.put(userId, webSocketClient);
        messagingTemplate.convertAndSend("/cfd/quotes/" + userId, Mappers.fromClientToMessage(webSocketClient));
    }

    @Scheduled(initialDelay = 1000, fixedRate = 5000)
    public void sendMessageToClients() {
        if (stockPricesListener.hasConnectionError()) {
            messagingTemplate.convertAndSend("/cfd/errors", new ConnectionError("error", "An error has occurred"));
            stockPricesListener.loadInstrumentsWithPrices();
        } else {
            for (long user : websocketService.getConnectedUsers()) {
                executorService.submit(() -> {
                    sendMessageToUser(user);
                });
            }
        }
    }
}