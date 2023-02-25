package com.t212.cfdaccounts.cfdaccounts.serviceclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.t212.cfdaccounts.cfdaccounts.api.rest.models.ApiResponse;
import com.t212.cfdaccounts.cfdaccounts.serviceclient.models.InstrumentWithPrice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AccountBalanceClient {
    @Autowired
    private RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AccountBalanceClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Acc getAccountBalance(long userId) throws JsonProcessingException {
        ApiResponse response = restTemplate.getForObject(instrumentPricesURL, ApiResponse.class);
        Map<String, InstrumentWithPrice> instrumentPrices = new ConcurrentHashMap<>();
        String result = objectMapper.writeValueAsString(response.getResult());
        List<InstrumentWithPrice> instruments = objectMapper.readValue(result, new TypeReference<List<InstrumentWithPrice>>() {
        });
        for (InstrumentWithPrice i : instruments) {
            instrumentPrices.put(i.ticker, i);
        }
        System.out.println(instrumentPrices.size());
        return instrumentPrices;
    }
}
