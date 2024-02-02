package com.service.order.controllers;

import com.service.order.advice.ErrorResponse;
import com.service.order.exception.Error;
import com.service.order.exception.OrderException;
import com.service.order.payloads.OrderDto;
import com.service.order.payloads.OrderRequest;
import com.service.order.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Set;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final io.github.resilience4j.circuitbreaker.CircuitBreaker circuitBreaker;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    @CircuitBreaker(name = "inventory", fallbackMethod = "inventoryFallback")
    @TimeLimiter(name = "inventory", fallbackMethod = "inventoryFallback")
    public Mono<OrderDto> createOrder(@RequestBody OrderRequest request,
                                      @RequestHeader("Authorization") String jwt){
        return orderService.createOrder(request, jwt)
                .transform(CircuitBreakerOperator.of(circuitBreaker));
        //enabling protection to the Mono using the rules provided in the circuit breaker instance
    }

    public Mono<ResponseEntity<ErrorResponse>> inventoryFallback(OrderRequest request, String jwt, Exception e){
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse(
                        Error.INVENTORY_SERVICE_CONNECTION_FAILURE,
                        "This service is unavailable at the moment please try after sometime!"
                )));
    }

}
