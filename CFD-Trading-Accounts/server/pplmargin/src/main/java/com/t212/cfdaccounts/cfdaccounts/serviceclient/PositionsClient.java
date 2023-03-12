package com.t212.cfdaccounts.cfdaccounts.serviceclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.t212.cfdaccounts.cfdaccounts.api.rest.models.ApiResponse;
import com.t212.cfdaccounts.cfdaccounts.repositories.models.AccountPositionDAO;
import com.t212.cfdaccounts.cfdaccounts.serviceclient.models.PositionWithPrices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PositionsClient {
    @Autowired
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    public PositionsClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Value("${positions.service.endpoint}")
    private String positionsURL;

    public List<AccountPositionDAO> getOpenPositions(long userId) throws JsonProcessingException, ResourceAccessException {
        String userPositionsUrl = positionsURL + userId + "/positions";
        ApiResponse response = restTemplate.getForObject(userPositionsUrl, ApiResponse.class);
        String result = objectMapper.writeValueAsString(response.getResult());
        List<AccountPositionDAO> positions = objectMapper.readValue(result, new TypeReference<>() {
        });
        return positions;
    }

    public Map<String, PositionWithPrices> getOpenPositionsWithPrices(long userId) throws JsonProcessingException {
        String userPositionsUrl = positionsURL + userId + "/open-positions";
        ApiResponse response = restTemplate.getForObject(userPositionsUrl, ApiResponse.class);
        Map<String, PositionWithPrices> userPositions = new ConcurrentHashMap<>();
        String result = objectMapper.writeValueAsString(response.getResult());
        List<PositionWithPrices> positions = objectMapper.readValue(result, new TypeReference<List<PositionWithPrices>>() {
        });
        for (PositionWithPrices p : positions) {
            userPositions.put(p.ticker, p);
        }
        return userPositions;
    }
}
