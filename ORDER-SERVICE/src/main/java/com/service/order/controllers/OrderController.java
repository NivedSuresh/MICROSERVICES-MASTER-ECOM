package com.service.order.controllers;

import com.service.order.payloads.OrderRequest;
import com.service.order.payloads.OrderDto;
import com.service.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    public OrderDto createOrder(@RequestBody OrderRequest request){
        return orderService.createOrder(request);
    }


}
