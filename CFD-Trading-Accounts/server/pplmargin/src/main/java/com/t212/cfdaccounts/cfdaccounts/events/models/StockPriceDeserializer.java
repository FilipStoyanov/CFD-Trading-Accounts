package com.t212.cfdaccounts.cfdaccounts.events.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.t212.cfdaccounts.cfdaccounts.events.StockPriceUpdateEvent;
import org.apache.kafka.common.serialization.Deserializer;
import java.io.IOException;

public class StockPriceDeserializer implements Deserializer<StockPriceUpdateEvent> {
    public static final ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @Override
    public StockPriceUpdateEvent deserialize(String s, byte[] bytes) {
        try {
            return mapper.readValue(bytes, StockPriceUpdateEvent.class);
        } catch (IOException e) {
            return null;
        }
    }
}
