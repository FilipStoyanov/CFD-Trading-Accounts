package com.t212.cfdaccounts.cfdaccounts.api.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.t212.cfdaccounts.cfdaccounts.api.websocket.models.InstrumentToPrice;
import com.t212.cfdaccounts.cfdaccounts.api.websocket.models.WebSocketClient;
import com.t212.cfdaccounts.cfdaccounts.api.websocket.services.WebSocketService;
import com.t212.cfdaccounts.cfdaccounts.core.Mappers;
import com.t212.cfdaccounts.cfdaccounts.core.AccountBalanceListener;
import com.t212.cfdaccounts.cfdaccounts.core.OpenPositionsListener;
import com.t212.cfdaccounts.cfdaccounts.core.StockPricesListener;
import com.t212.cfdaccounts.cfdaccounts.core.models.OpenPositionPPL;
import com.t212.cfdaccounts.cfdaccounts.events.AccountBalanceUpdaterEvent;
import com.t212.cfdaccounts.cfdaccounts.events.PositionsUpdaterEvent;
import com.t212.cfdaccounts.cfdaccounts.events.models.PositionsUpdateType;
import com.t212.cfdaccounts.cfdaccounts.repositories.AccountCashRepository;
import com.t212.cfdaccounts.cfdaccounts.repositories.AccountPositionRepository;
import com.t212.cfdaccounts.cfdaccounts.repositories.models.AccountPositionDAO;
import com.t212.cfdaccounts.cfdaccounts.serviceclient.PositionsClient;
import com.t212.cfdaccounts.cfdaccounts.serviceclient.models.InstrumentWithPrice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketHandler {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private StockPricesListener stockPricesListener;

    @Autowired
    private OpenPositionsListener positionsListener;
    @Autowired
    private AccountBalanceListener accountBalanceListener;
    private final WebSocketService websocketService;
    private final AccountPositionRepository accountPositionRepository;

    private final PositionsClient positionClient;
    private final AccountCashRepository accountCashRepository;

    private Map<Long, WebSocketClient> messagesForUsers;
    private PositionsUpdaterEvent event;

    public WebSocketHandler(WebSocketService websocketService, AccountPositionRepository accountPositionRepository, AccountCashRepository accountCashRepository, PositionsClient positionClient) {
        this.websocketService = websocketService;
        this.messagesForUsers = new ConcurrentHashMap<>();
        this.accountPositionRepository = accountPositionRepository;
        this.accountCashRepository = accountCashRepository;
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
            lockedCash = lockedCash.add(position.getValue().margin);
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
            System.out.println("URL: " + subscribedUrl);
            List<String> urlParts = Arrays.stream(subscribedUrl.split("/")).toList();
            long userId = Long.parseLong(urlParts.get(urlParts.size() - 1));
            WebSocketClient message = new WebSocketClient();
            try {
                List<AccountPositionDAO> openPositionsForCurrentUser = positionClient.getOpenPositions(userId);
                message.setPositions(openPositionsForCurrentUser);
                Map<String, OpenPositionPPL> openPositionsWithPPLAndMargin = calculateOpenPositionsMarginAndPPL(message.getPositions());
                System.out.println("SIZE: " + openPositionsWithPPLAndMargin.size());
                message.setOpenPositions(openPositionsWithPPLAndMargin);
//                BigDecimal accountCash = accountCashRepository.getById(userId).getBalance();
//                Map<String, BigDecimal> balanceAndStatus = calculateCashAndStatus(openPositionsWithPPLAndMargin, accountCash);
//                message.setFreeCash(balanceAndStatus.get("freeCash"));
//                message.setLockedCash(balanceAndStatus.get("lockedCash"));
//                message.setStatus(balanceAndStatus.get("status"));
//                messagesForUsers.put(userId, message);
//                websocketService.addConnection(userId);
            } catch (JsonProcessingException e) {
                System.out.println("JsonProcessingException: ");
                //todo: handle this exception
            }
        }
    }

    private Map<String, AccountPositionDAO> updateUserPositions(long userId) {
        PositionsUpdaterEvent event = positionsListener.getPositionsUpdaterEventsForUser(userId);
        WebSocketClient currentUser = messagesForUsers.get(userId);
        Map<String, AccountPositionDAO> openPositionsForUser = currentUser.getPositions();
        if (event == null) {
            return openPositionsForUser;
        }
        if (event.type.equals(PositionsUpdateType.OPEN)) {
            InstrumentWithPrice instrument = stockPricesListener.getInstrumentPrices().get(event.ticker);
            openPositionsForUser.put(event.ticker, new AccountPositionDAO(event.userId, event.ticker, event.quantity, event.positionType, event.buyPrice, event.sellPrice, new Date(event.timestamp)));
        } else {
            openPositionsForUser.remove(event.ticker);
        }
        return openPositionsForUser;
    }

    private BigDecimal updateAccountBalance(long userId) {
        AccountBalanceUpdaterEvent event = accountBalanceListener.getAccountBalanceEventForUser(userId);
        if (event != null) {
            return event.balance;
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
        webSocketClient.setPositions(userOpenPositions);
        Map<String, OpenPositionPPL> openPositionsWithPPLAndMargin = calculateOpenPositionsMarginAndPPL(userOpenPositions);
        webSocketClient.setOpenPositions(openPositionsWithPPLAndMargin);
        BigDecimal updatedAccountBalance = updateAccountBalance(userId);
        Map<String, BigDecimal> balance;
        if (updatedAccountBalance != null) {
            balance = calculateCashAndStatus(openPositionsWithPPLAndMargin, updatedAccountBalance);
        } else {
            BigDecimal accountBalance = messagesForUsers.get(userId).getFreeCash().add(messagesForUsers.get(userId).getLockedCash());
            balance = calculateCashAndStatus(openPositionsWithPPLAndMargin, accountBalance);
        }
        webSocketClient.setFreeCash(balance.get("freeCash"));
        webSocketClient.setLockedCash(balance.get("lockedCash"));
        webSocketClient.setStatus(balance.get("status"));
        synchronized (messagesForUsers) {
            messagesForUsers.put(userId, webSocketClient);
            messagingTemplate.convertAndSend("/cfd/quotes/" + userId, Mappers.fromClientToMessage(webSocketClient));
        }
    }

//    @Scheduled(cron = "0/1 * 10-18 * * *")
//    public void sendMessageToClients() {
//        ExecutorService executor = Executors.newFixedThreadPool(10);
//        for (Long user : websocketService.getConnectedUsers()) {
//            executor.execute(() -> {
//                sendMessageToUser(user);
//            });
//        }
//    }
}