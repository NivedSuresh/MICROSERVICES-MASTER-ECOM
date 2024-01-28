package com.service.product.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class RestClientConfig {

    @LoadBalanced
    @Bean("InventoryService")
    public RestClient restClient(RestClient.Builder builder){
        return builder.baseUrl("http://inventory-service/api/inventory")
                .build();
    }

    @Bean
    @LoadBalanced
    public RestClient.Builder builder(){
        return RestClient.builder();
    }

}
