package com.t212.websocketgateway.Websocket.Gateway;

import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class WebsocketGatewayApplication {

    @Autowired
    private EurekaClient clients;

    public int getCountOfClients() {
        return clients.getApplication("websocket-server").getInstances().size();
    }

    public static void main(String[] args) {
        SpringApplication.run(WebsocketGatewayApplication.class, args);
    }
}
