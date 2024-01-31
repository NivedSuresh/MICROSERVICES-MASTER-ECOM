package com.api.gateway.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class RestClientConfig {

    @LoadBalanced
    @Bean
    public WebClient restClient(WebClient.Builder builder){
        return builder.baseUrl("http://api-gateway/api")
                .build();
    }

    @Bean
    @LoadBalanced
    public WebClient.Builder builder(){
        return WebClient.builder();
    }

}
