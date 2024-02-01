package com.service.order.mapper.impls;

import com.service.order.exception.Error;
import com.service.order.exception.OrderException;
import com.service.order.mapper.Mapper;
import com.service.order.models.Order;
import com.service.order.models.OrderItem;
import com.service.order.payloads.OrderDto;
import com.service.order.payloads.OrderItemDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderMapper implements Mapper {
    @Override
    public OrderDto orderEntityToDto(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .totalPrice(order.getTotalPrice())
                .orderItems(orderItemsEntityListToDtoList(order.getOrderItems()))
                .build();
    }

    public List<OrderItemDto> orderItemsEntityListToDtoList(List<OrderItem> orderItems) {
        return orderItems.stream().map(this::orderItemToDto).collect(Collectors.toList());
    }

    public OrderItemDto orderItemToDto(OrderItem orderItem) {
        return OrderItemDto.builder()
                .skuCode(orderItem.getSkuCode())
                .totalPrice(orderItem.getTotalPrice())
                .quantityPerItem(orderItem.getQuantityPerItem())
                .build();
    }

    @Override
    public List<OrderItem> toOrderItemEntity(List<OrderItemDto> orderItems, Order order) {
        return orderItems.stream().map(orderItemDto -> toOrderItemDto(orderItemDto, order))
                                                                    .collect(Collectors.toList());
    }

    public OrderItem toOrderItemDto(OrderItemDto orderItemDto, Order order) {
        return OrderItem.builder()
                .quantityPerItem(orderItemDto.getQuantityPerItem())
                .skuCode(orderItemDto.getSkuCode())
                .order(order)
                .totalPrice(orderItemDto.getTotalPrice())
                .build();
    }

    @Override
    public List<String> getSkuCodes(List<OrderItemDto> orderItemsDtoList) {
        return orderItemsDtoList.stream().map(OrderItemDto::getSkuCode).collect(Collectors.toList());
    }

    @Override
    public Double getOrderTotalPrice(List<OrderItemDto> orderItemsDtoList) {
        Optional<Double> totalPrice = orderItemsDtoList.stream()
                .map(OrderItemDto::getTotalPrice)
                .reduce(Double::sum);
        return totalPrice.orElse(null);
    }

    @Override
    public Map<String, Integer> getRequiredStockInfo(List<OrderItemDto> orderItemsDtoList) {
        try{
            return orderItemsDtoList.stream().
                    collect(Collectors.toMap(OrderItemDto::getSkuCode,
                            OrderItemDto::getQuantityPerItem));
        }catch (IllegalStateException e){
            e.printStackTrace();
            log.error("Error while mapping OrderItems to HashMap of Required Stock : {}", e.getMessage());
            throw new OrderException(
                    "There was an issue with your request, try clearing your cart and send again!",
                    HttpStatus.BAD_REQUEST.value(),
                    Error.INVALID_REQUEST_RECEIVED
            );
        }
    }
}
