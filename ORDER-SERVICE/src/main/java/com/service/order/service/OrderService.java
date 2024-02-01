package com.service.order.service;

import com.service.order.payloads.OrderDto;
import com.service.order.payloads.OrderRequest;
import reactor.core.publisher.Mono;

public interface OrderService {
    Mono<OrderDto> createOrder(OrderRequest request, String jwt);
}
