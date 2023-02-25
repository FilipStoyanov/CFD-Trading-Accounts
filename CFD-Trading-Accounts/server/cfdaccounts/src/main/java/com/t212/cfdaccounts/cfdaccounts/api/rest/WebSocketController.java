package com.t212.cfdaccounts.cfdaccounts.api.rest;

import com.t212.cfdaccounts.cfdaccounts.api.rest.models.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

@RestController
@CrossOrigin(value = "*")
@RequestMapping(value = "/api/v1/websocket-gateway")
public class WebSocketController {

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping
    public ResponseEntity<ApiResponse> openConnection() {
        List<ServiceInstance> websocketInstances = discoveryClient.getInstances("websocket-server");
        if (websocketInstances.size() == 0) {
            return ResponseEntity.status(404).body(new ApiResponse(404, "Not found websocket server"));
        }
        String websocketURI = websocketInstances.get(new Random().nextInt(0, websocketInstances.size())).getUri().toString() + "/websocket/";
        return ResponseEntity.status(200).body(new ApiResponse(200, "", websocketURI));

    }
}