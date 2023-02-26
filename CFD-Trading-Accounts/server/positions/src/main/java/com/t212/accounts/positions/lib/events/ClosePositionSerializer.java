package com.t212.accounts.positions.lib.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

public class ClosePositionSerializer implements Serializer<ClosePositionEvent> {

    public static final ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @Override
    public byte[] serialize(String s, ClosePositionEvent closePositionSerializer) {
        try {
            return mapper.writeValueAsBytes(closePositionSerializer);
        } catch (JsonProcessingException e) {
            throw new SerializationException(e);
        }
    }
}

