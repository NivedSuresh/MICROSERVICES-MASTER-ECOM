package com.api.gateway.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.*;

@Configuration
@AllArgsConstructor
public class Beans {

    private final RsaKeyProperties rsaKeys;
    @Bean
    public ReactiveJwtDecoder jwtDecoder(){
        return NimbusReactiveJwtDecoder.withPublicKey(rsaKeys.publicKey()).build();
    }

}
