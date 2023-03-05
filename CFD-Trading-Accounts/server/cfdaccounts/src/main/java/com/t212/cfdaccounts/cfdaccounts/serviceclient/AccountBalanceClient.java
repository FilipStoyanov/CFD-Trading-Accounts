package com.t212.cfdaccounts.cfdaccounts.serviceclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.t212.cfdaccounts.cfdaccounts.api.rest.models.ApiResponse;
import com.t212.cfdaccounts.cfdaccounts.serviceclient.models.AccountBalance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
public class AccountBalanceClient {
    @Autowired
    private RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${balance.service.endpoint}")
    private String userBalanceURL;
    public AccountBalanceClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public AccountBalance getAccountBalance(long userId) throws JsonProcessingException, ResourceAccessException {
        final String accountBalanceURL = userBalanceURL + userId + "/balance";
        ApiResponse response = restTemplate.getForObject(accountBalanceURL, ApiResponse.class);
        String result = objectMapper.writeValueAsString(response.getResult());
        AccountBalance balance = objectMapper.readValue(result, new TypeReference<>() {
        });
        return balance;
    }
}
