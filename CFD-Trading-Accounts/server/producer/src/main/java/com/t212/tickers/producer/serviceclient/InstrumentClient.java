package com.t212.tickers.producer.serviceclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.t212.tickers.producer.serviceclient.models.ApiResponse;
import com.t212.tickers.producer.serviceclient.models.Instrument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class InstrumentClient {
    @Autowired
    private RestTemplate restTemplate;
    @Value("${instruments-prices.service.endpoint}")
    private String instrumentPricesURL;

    private final ObjectMapper objectMapper;

    public InstrumentClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<Instrument> getInstruments() throws JsonProcessingException, ResourceAccessException {
        ApiResponse response = restTemplate.getForObject(instrumentPricesURL, ApiResponse.class);
        String result = objectMapper.writeValueAsString(response.getResult());
        List<Instrument> instrument = objectMapper.readValue(result, new TypeReference<List<Instrument>>() {
        });
        System.out.println(instrument.size());
        return instrument;
    }

}
