package com.t212.cfdaccounts.cfdaccounts;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = {"com.t212.cfdaccounts.cfdaccounts.bin.*"}))
public class CfdaccountsApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(CfdaccountsApplication.class)
                .properties("spring.application.name=websocket-server")
                .run(args);
    }
}
