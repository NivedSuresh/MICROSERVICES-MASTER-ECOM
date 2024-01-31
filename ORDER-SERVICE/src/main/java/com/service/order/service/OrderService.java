package com.service.order.service;

import com.service.order.payloads.OrderRequest;
import com.service.order.payloads.OrderDto;

public interface OrderService {
    OrderDto createOrder(OrderRequest request, String jwt);
}
