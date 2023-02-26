package com.t212.account.balance.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

public class AccountBalanceSerializer implements Serializer<AccountBalanceUpdaterEvent> {

    public static final ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @Override
    public byte[] serialize(String s, AccountBalanceUpdaterEvent accountBalanceUpdaterEvent) {
        try {
            return mapper.writeValueAsBytes(accountBalanceUpdaterEvent);
        } catch (JsonProcessingException e) {
            throw new SerializationException(e);
        }
    }
}
