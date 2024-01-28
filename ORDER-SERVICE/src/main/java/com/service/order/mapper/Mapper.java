package com.service.order.mapper;

import com.service.order.models.Order;
import com.service.order.models.OrderItem;
import com.service.order.payloads.OrderDto;
import com.service.order.payloads.OrderItemDto;

import java.util.List;
import java.util.Map;

public interface Mapper {
    OrderDto orderEntityToDto(Order order);
    List<OrderItemDto> orderItemsEntityListToDtoList(List<OrderItem> orderItems);
    OrderItemDto orderItemToDto(OrderItem orderItem);

    List<OrderItem> toOrderItemEntity(List<OrderItemDto> orderItems, Order order);

    OrderItem toOrderItemDto(OrderItemDto orderItemDto, Order order);

    List<String> getSkuCodes(List<OrderItemDto> orderItemsDtoList);

    Double getOrderTotalPrice(List<OrderItemDto> orderItemsDtoList);

    Map<String, Integer> getRequiredStockInfo(List<OrderItemDto> orderItemsDtoList);
}
