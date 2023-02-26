package com.t212.cfdaccounts.cfdaccounts.api.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.t212.cfdaccounts.cfdaccounts.api.websocket.models.WebSocketClient;
import com.t212.cfdaccounts.cfdaccounts.api.websocket.services.WebSocketService;
import com.t212.cfdaccounts.cfdaccounts.core.Mappers;
import com.t212.cfdaccounts.cfdaccounts.core.AccountBalanceListener;
import com.t212.cfdaccounts.cfdaccounts.core.PositionsListener;
import com.t212.cfdaccounts.cfdaccounts.core.StockPricesListener;
import com.t212.cfdaccounts.cfdaccounts.core.models.OpenPositionPPL;
import com.t212.cfdaccounts.cfdaccounts.events.AccountBalanceUpdaterEvent;
import com.t212.cfdaccounts.cfdaccounts.repositories.models.AccountPositionDAO;
import com.t212.cfdaccounts.cfdaccounts.serviceclient.PositionsClient;
import com.t212.cfdaccounts.cfdaccounts.serviceclient.models.InstrumentWithPrice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
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

    @Autowired
    private StockPricesListener stockPricesListener;

    @Autowired
    private PositionsListener positionsListener;
    @Autowired
    private AccountBalanceListener accountBalanceListener;
    private final WebSocketService websocketService;

    private final PositionsClient positionClient;
    private Map<Long, WebSocketClient> messagesForUsers;

    public PPLAndMarginHandler(WebSocketService websocketService, PositionsClient positionClient) {
        this.websocketService = websocketService;
        this.messagesForUsers = new ConcurrentHashMap<>();
        this.positionClient = positionClient;
    }

    private Map<String, OpenPositionPPL> calculateOpenPositionsMarginAndPPL(Map<String, AccountPositionDAO> mapOfOpenPositions) {
        Map<String, OpenPositionPPL> openPositionPPL = new ConcurrentHashMap<>();
        BigDecimal price, currentPrice, spread, result, margin;
        InstrumentWithPrice currentInstrument;
        for (Map.Entry<String, AccountPositionDAO> position : mapOfOpenPositions.entrySet()) {
            if (stockPricesListener.getInstrumentPrices().containsKey(position.getValue().ticker)) {
                currentInstrument = stockPricesListener.getInstrumentPrices().get(position.getValue().ticker);
                price = position.getValue().type.equals("LONG") ? position.getValue().buyPrice : position.getValue().sellPrice;
                currentPrice = position.getValue().type.equals("LONG") ? currentInstrument.buy : currentInstrument.sell;
                // spread = (buyPrice - sellPrice) * quantity
                spread = (position.getValue().buyPrice.subtract(position.getValue().sellPrice)).multiply(position.getValue().quantity);
                // result = (currentPrice - price) * quantity + spread
                result = (currentPrice.subtract(price)).multiply(position.getValue().quantity).add(spread);
                //margin = ask/bid price * leverage
                margin = position.getValue().quantity.multiply(currentPrice.multiply(currentInstrument.leverage));
                openPositionPPL.put(position.getValue().ticker, new OpenPositionPPL(position.getValue().ticker, position.getValue().quantity, position.getValue().type, price, currentPrice, margin, result));
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


    private Map<String, BigDecimal> calculateCashAndStatus(Map<String, OpenPositionPPL> openPositions, BigDecimal accountBalance) {
        Map<String, BigDecimal> balance = new ConcurrentHashMap<>();
        BigDecimal lockedCash = new BigDecimal(0);
        for (Map.Entry<String, OpenPositionPPL> position : openPositions.entrySet()) {
            lockedCash = lockedCash.add(position.getValue().margin());
        }
        balance.put("lockedCash", lockedCash);
        balance.put("freeCash", accountBalance.subtract(lockedCash));
        balance.put("status", calculateStatus(lockedCash, accountBalance));
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
                System.out.println("JsonProcessingException: ");
                //todo: handle this exception
            }
        }
    }

    private Map<String, AccountPositionDAO> updateUserPositions(long userId) {
        Map<String, AccountPositionDAO> positions = positionsListener.getUserPositions(userId);
        return positions;
    }

    private BigDecimal updateAccountBalance(long userId) {
        AccountBalanceUpdaterEvent event = accountBalanceListener.getAccountBalanceEventForUser(userId);
        if (event != null) {
            return event.balance();
        }
        return null;
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
        synchronized (messagesForUsers) {
            messagesForUsers.put(userId, webSocketClient);
            messagingTemplate.convertAndSend("/cfd/quotes/" + userId, Mappers.fromClientToMessage(webSocketClient));
        }
    }

    @Scheduled(initialDelay = 1000, fixedRate = 1000)
    public void sendMessageToClients() {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (Long user : websocketService.getConnectedUsers()) {
            executor.execute(() -> {
                sendMessageToUser(user);
            });
        }
    }
}