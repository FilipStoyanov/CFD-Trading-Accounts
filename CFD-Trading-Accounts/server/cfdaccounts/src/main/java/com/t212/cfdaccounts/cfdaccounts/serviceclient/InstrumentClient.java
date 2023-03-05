package com.t212.cfdaccounts.cfdaccounts.serviceclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.t212.cfdaccounts.cfdaccounts.api.rest.models.ApiResponse;
import com.t212.cfdaccounts.cfdaccounts.serviceclient.models.InstrumentWithPrice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InstrumentClient {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${instruments-prices.service.endpoint}")
    private String instrumentPricesURL;

    @Value("${instrument.service.endpoint}")
    private String mostUsedInstrumentsURL;

    private final ObjectMapper objectMapper;

    public InstrumentClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<InstrumentWithPrice> getAllInstruments() throws JsonProcessingException, DataAccessException {
        ApiResponse response = restTemplate.getForObject(instrumentPricesURL, ApiResponse.class);
        Map<String, InstrumentWithPrice> instrumentPrices = new ConcurrentHashMap<>();
        String result = objectMapper.writeValueAsString(response.getResult());
        List<InstrumentWithPrice> instruments = objectMapper.readValue(result, new TypeReference<List<InstrumentWithPrice>>() {
        });
        return instruments;
    }

    public Map<String, InstrumentWithPrice> getInstruments() throws JsonProcessingException, DataAccessException {
        ApiResponse response = restTemplate.getForObject(instrumentPricesURL, ApiResponse.class);
        Map<String, InstrumentWithPrice> instrumentPrices = new ConcurrentHashMap<>();
        String result = objectMapper.writeValueAsString(response.getResult());
        List<InstrumentWithPrice> instruments = objectMapper.readValue(result, new TypeReference<List<InstrumentWithPrice>>() {
        });
        for (InstrumentWithPrice i : instruments) {
            instrumentPrices.put(i.ticker, i);
        }
        return instrumentPrices;
    }

    public Map<String, InstrumentWithPrice> getMostUsedInstruments() throws JsonProcessingException, ResourceAccessException {
        ApiResponse response = restTemplate.getForObject(mostUsedInstrumentsURL, ApiResponse.class);
        Map<String, InstrumentWithPrice> mostUsedInstruments = new ConcurrentHashMap<>();
        String result = objectMapper.writeValueAsString(response.getResult());
        List<InstrumentWithPrice> instruments = objectMapper.readValue(result, new TypeReference<List<InstrumentWithPrice>>() {
        });
        for (InstrumentWithPrice i : instruments) {
            mostUsedInstruments.put(i.ticker, i);
        }
        return mostUsedInstruments;
    }

    public InstrumentWithPrice getInstrumentWithPrice(String ticker) throws JsonProcessingException, ResourceAccessException {
        ApiResponse response = restTemplate.getForObject(instrumentPricesURL + "/" + ticker, ApiResponse.class);
        String result = objectMapper.writeValueAsString(response.getResult());
        InstrumentWithPrice instrument = objectMapper.readValue(result, new TypeReference<InstrumentWithPrice>() {
        });
        return instrument;
    }
}
