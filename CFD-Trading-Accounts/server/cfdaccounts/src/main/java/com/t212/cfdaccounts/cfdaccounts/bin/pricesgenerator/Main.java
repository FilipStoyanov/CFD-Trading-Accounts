package com.t212.cfdaccounts.cfdaccounts.bin.pricesgenerator;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.t212.cfdaccounts.cfdaccounts.kafka", "com.t212.cfdaccounts.cfdaccounts.gateways", "com.t212.cfdaccounts.cfdaccounts.bin.pricesgenerator", "com.t212.cfdaccounts.cfdaccounts.events"})
@EnableKafka
@EnableScheduling
public class Main {
    public static void main(String[] args) {
        new SpringApplicationBuilder(Main.class)
                .properties("server.port=8085", "spring.application.name=price-generator")
                .web(WebApplicationType.SERVLET)
                .run(args);
    }
}