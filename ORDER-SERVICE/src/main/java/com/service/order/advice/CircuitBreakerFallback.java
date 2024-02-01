package com.service.order.advice;

import com.service.order.payloads.OrderDto;
import com.service.order.payloads.OrderRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import reactor.core.publisher.Mono;

public class CircuitBreakerFallback {
    public Mono<OrderDto> fallBack(OrderRequest request,
                                   String jwt, RuntimeException exception){
        System.out.println("Fallback triggered");
        return Mono.just(new OrderDto());
    }

}
