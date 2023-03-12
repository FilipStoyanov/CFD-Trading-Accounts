package com.t212.instruments.instruments.manager.lib.events.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.t212.instruments.instruments.manager.lib.events.StockPriceUpdateEvents;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

public class StockPriceDeserializer implements Deserializer<StockPriceUpdateEvents> {
    public static final ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @Override
    public StockPriceUpdateEvents deserialize(String s, byte[] bytes) {
        try {
            return mapper.readValue(bytes, StockPriceUpdateEvents.class);
        } catch (IOException e) {
            return null;
        }
    }
}
