package com.t212.accounts.positions.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    public CorsConfig() {
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping(Routes.APP_BASE_ROUTE + Routes.ALL_SUB_ROUTES).allowedOrigins("*").allowedMethods("*");
    }

    private static class Routes {
        private static final String APP_BASE_ROUTE = "/";

        private static final String ALL_SUB_ROUTES = "**";

        public Routes() {
        }
    }
}