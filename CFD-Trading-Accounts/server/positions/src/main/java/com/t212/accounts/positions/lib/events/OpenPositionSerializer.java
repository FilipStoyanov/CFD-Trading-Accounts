package com.t212.accounts.positions.lib.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

public class OpenPositionSerializer implements Serializer<OpenPositionEvent> {

    public static final ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @Override
    public byte[] serialize(String s, OpenPositionEvent openPositionSerializer) {
        try {
            return mapper.writeValueAsBytes(openPositionSerializer);
        } catch (JsonProcessingException e) {
            throw new SerializationException(e);
        }
    }
}
