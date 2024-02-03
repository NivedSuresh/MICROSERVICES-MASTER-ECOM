package com.service.auth.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.support.ExecutorServiceAdapter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class KafkaExecutorConfig {

    @Bean
    public ScheduledExecutorService kafkaExecutorService(){
        return Executors.newScheduledThreadPool(4);
    }


}
