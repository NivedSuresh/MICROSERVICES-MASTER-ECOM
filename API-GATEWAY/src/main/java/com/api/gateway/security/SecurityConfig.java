package com.api.gateway.security;


import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final ReactiveJwtDecoder jwtDecoder;
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity security){

        security.csrf(ServerHttpSecurity.CsrfSpec::disable);

        security.oauth2ResourceServer(oAuth ->
                oAuth.jwt(jwt -> jwt.jwtDecoder(jwtDecoder)
                        .jwtAuthenticationConverter(
                                source -> Mono.just(
                                        new JwtAuthenticationToken(source, extractAuthorities(source))
                                ))));

        security.authorizeExchange(exc ->
            exc.pathMatchers("/eureka/**",
                    "/api/auth/login",
                    "/api/auth/signup").permitAll()
                    .pathMatchers("/api/product/create").hasAuthority("ADMIN")
                    .anyExchange().authenticated());

        return security.build();
    }

    private Collection<? extends GrantedAuthority> extractAuthorities(Jwt jwt) {
        String claim = jwt.getClaim("authority");
        return List.of(new SimpleGrantedAuthority(claim));
    }

}
