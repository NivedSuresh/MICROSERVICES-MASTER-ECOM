package com.service.order.controllers;

import com.service.order.payloads.OrderRequest;
import com.service.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    public Mono<String> createOrder(@RequestBody OrderRequest request,
                                    @RequestHeader("Authorization") String jwt){
        return orderService.createOrder(request, jwt);
    }


}
