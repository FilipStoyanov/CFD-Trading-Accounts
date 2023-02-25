package com.t212.cfdaccounts.cfdaccounts.api.websocket.services;

import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class WebSocketService {
    private Set<Long> connectedUsers;

    public WebSocketService() {
        this.connectedUsers = new CopyOnWriteArraySet<>();
    }

    public WebSocketService(Set<Long> connectedUser) {
        this.connectedUsers = connectedUser;
    }

    public Set<Long> getConnectedUsers() {
        return connectedUsers;
    }

    public void addConnection(long user) {
        connectedUsers.add(user);
    }

    public void removeConnection(Integer userId) {
        connectedUsers.remove(userId);
    }
}
