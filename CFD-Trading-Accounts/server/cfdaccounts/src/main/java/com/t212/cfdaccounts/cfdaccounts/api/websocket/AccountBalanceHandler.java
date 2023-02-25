package com.t212.cfdaccounts.cfdaccounts.api.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class AccountBalanceHandler {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;


}
