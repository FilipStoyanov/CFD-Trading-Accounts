package com.t212.cfdaccounts.cfdaccounts.core;

import com.t212.cfdaccounts.cfdaccounts.api.websocket.models.Message;
import com.t212.cfdaccounts.cfdaccounts.api.websocket.models.WebSocketClient;
import com.t212.cfdaccounts.cfdaccounts.core.models.Instrument;
import com.t212.cfdaccounts.cfdaccounts.core.models.User;
import com.t212.cfdaccounts.cfdaccounts.repositories.models.InstrumentDAO;
import com.t212.cfdaccounts.cfdaccounts.repositories.models.UserDAO;

public final class Mappers {
    public static User fromResultSetToUser(UserDAO user) {
        return new User(user.id, user.username, user.passwordHash, user.nationalId, user.email);
    }

    public static Instrument fromResultSetToInstrument(InstrumentDAO instrument) {
        return new Instrument(instrument.id, instrument.name, instrument.ticker, instrument.fullname, instrument.quantity, instrument.leverage, instrument.marketName,
                instrument.createdAt, instrument.updatedAt);
    }


    public static Message fromClientToMessage(WebSocketClient client) {
        return new Message(client.getOpenPositions(), client.getFreeCash(), client.getLockedCash(), client.getStatus());
    }
}
