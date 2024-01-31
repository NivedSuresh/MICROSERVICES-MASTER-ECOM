package com.service.order.controllers;

import com.service.order.payloads.OrderRequest;
import com.service.order.payloads.OrderDto;
import com.service.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    public OrderDto createOrder(@RequestBody OrderRequest request, @RequestHeader("Authorization") String jwt){
        return orderService.createOrder(request, jwt);
    }


}
