package com.t212.cfdaccounts.cfdaccounts.events.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.t212.cfdaccounts.cfdaccounts.events.AccountBalanceUpdaterEvent;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

public class AccountBalanceDeserializer implements Deserializer<AccountBalanceUpdaterEvent> {
    public static final ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @Override
    public AccountBalanceUpdaterEvent deserialize(String s, byte[] bytes) {
        try {
            return mapper.readValue(bytes, AccountBalanceUpdaterEvent.class);
        } catch (IOException e) {
            return null;
        }
    }
}
