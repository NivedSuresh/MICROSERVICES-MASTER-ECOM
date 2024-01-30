package com.service.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestAuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(AuthServiceApplication::main).with(TestAuthServiceApplication.class).run(args);
    }

}
