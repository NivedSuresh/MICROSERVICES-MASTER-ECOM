package com.service.order.service;

import com.service.order.payloads.OrderRequest;
import reactor.core.publisher.Mono;

public interface OrderService {
    Mono<String> createOrder(OrderRequest request, String jwt);
}
