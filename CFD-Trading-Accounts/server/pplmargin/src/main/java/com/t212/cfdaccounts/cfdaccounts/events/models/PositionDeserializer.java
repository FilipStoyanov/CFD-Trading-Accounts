package com.t212.cfdaccounts.cfdaccounts.events.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.t212.cfdaccounts.cfdaccounts.events.PositionUpdateEvent;
import org.apache.kafka.common.serialization.Deserializer;
import java.io.IOException;

public class PositionDeserializer implements Deserializer<PositionUpdateEvent> {
    public static final ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @Override
    public PositionUpdateEvent deserialize(String s, byte[] bytes) {
        try {
            return mapper.readValue(bytes, PositionUpdateEvent.class);
        } catch (IOException e) {
            return null;
        }
    }
}
