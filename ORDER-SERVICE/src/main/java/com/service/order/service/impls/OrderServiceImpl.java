package com.service.order.service.impls;

import com.service.order.advice.ErrorResponse;
import com.service.order.events.NotificationEvent;
import com.service.order.exception.*;
import com.service.order.exception.Error;
import com.service.order.mapper.Mapper;
import com.service.order.models.Order;
import com.service.order.payloads.InventoryResponse;
import com.service.order.payloads.OrderDto;
import com.service.order.payloads.OrderRequest;
import com.service.order.repo.OrderRepo;
import com.service.order.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepo orderRepo;
    private final Mapper mapper;
    private final WebClient InventoryServiceWebClient;
    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public OrderServiceImpl(OrderRepo orderRepo, Mapper mapper, WebClient.Builder builder,
                            KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
        this.orderRepo = orderRepo;
        this.mapper = mapper;
        this.InventoryServiceWebClient = builder
                .baseUrl("http://api-gateway/api")
                .build();
        this.kafkaTemplate = kafkaTemplate;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Mono<OrderDto> createOrder(OrderRequest request, String jwt) {

        log.info("Received OrderRequest : {}", request);

        Map<String, Integer> requiredStockInfo =
                mapper.getRequiredStockInfo(request.getOrderItemsDtoList());

        return getAvailableStockInfoFromInventory(requiredStockInfo.keySet(), jwt)

                .handle((inventoryResponses, synchronousSink) ->
                    verifyRequestAndStock(inventoryResponses, requiredStockInfo)
                )


                .then(getTotalPrice(request))


                .publishOn(Schedulers.boundedElastic())
                .map((aDouble) ->
                    mapper.orderEntityToDto(saveOrderToDb(createOrder(request, aDouble)))
                )


                .map(orderDto -> {
                    //email should be extracted from the jwt
                    sendNotificationAfterOrderSuccession(jwt, orderDto.getTotalPrice());
                    return orderDto;
                })


                .doFinally(signalType -> {
                    if(signalType == SignalType.ON_COMPLETE)
                        log.info("Order creation success!");

                    else log.error("Order creation failure!");
                });
    }

    private void sendNotificationAfterOrderSuccession(String email, Double totalPrice) {
        kafkaTemplate.send("notification", NotificationEvent.builder()
                .email(email)
                .notification(createOrderSuccessionMail(totalPrice))
                .build()
        );
    }


    private String createOrderSuccessionMail(Double totalPrice) {
        return new StringBuilder("Thank you for shopping with us, your order for ₹")
                .append(totalPrice)
                .append(" has been placed!\nIf this was unintended you can get back to us through the app!")
                .toString();
    }


    private Mono<Double> getTotalPrice(OrderRequest request){
        Double total = mapper.getOrderTotalPrice(request.getOrderItemsDtoList());
        if(total == null || total<=0) throw new UnableToPlaceOrderException(
                "Malfunction!",
                HttpStatus.BAD_REQUEST.value(),
                Error.ORDER_ITEM_LIST_PRICE_EMPTY
        );
        return Mono.just(total);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Order saveOrderToDb(Order order) {
        try{ return orderRepo.save(order); }
        catch (Exception e){
            throw new UnableToPlaceOrderException(
                    "Unable to place order at this point of time, try again later!",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    Error.FAILED_TO_WRITE_ORDER
            );
        }
    }

    private Order createOrder(OrderRequest request, Double totalPrice){
        Order order = new Order();
        order.setOrderItems(mapper.toOrderItemEntity(request.getOrderItemsDtoList(), order));
        order.setTotalPrice(totalPrice);
        return order;
    }


    /* Will throw an exception if there aren't enough products in quantity for the specified
            SkuCode or if the Request is not valid, ie -> purchase quantity should be >= 1 */
    private void verifyRequestAndStock(List<InventoryResponse> stockInfo, Map<String, Integer> requiredStockInfo) {

        log.info("Received quantity info in verify method : {}",stockInfo);

        if(requiredStockInfo.size() > stockInfo.size()) throw new LackOfInformationFromDbException(
                "Failed to find information on all required products",
                HttpStatus.BAD_REQUEST.value(),
                Error.INVALID_REQUEST_RECEIVED
        );


        for(InventoryResponse stock : stockInfo){
            if(requiredStockInfo.get(stock.getSkuCode()) < 1) throw new UnableToPlaceOrderException(
                    "The product matching the SkuCode ".concat(stock.getSkuCode()).
                            concat(" cannot be purchased with a quantity less than 1"),
                    HttpStatus.BAD_REQUEST.value(),
                    Error.INVALID_REQUEST_RECEIVED
            );
            if(stock.getQuantity() < requiredStockInfo.get(stock.getSkuCode()))
                throw new OutOfStockException(
                        "The product matching the SkuCode ".concat(stock.getSkuCode()).
                                concat(" does not have enough quantity to make this purchase"),
                        HttpStatus.BAD_REQUEST.value(),
                        Error.NOT_ENOUGH_STOCK
                );
        }
        log.info("All products have desired amount of quantity.");
    }



    @Retry(name = "inventory")
    public Mono<List<InventoryResponse>> getAvailableStockInfoFromInventory(Set<String> skuCodes, String jwt) {
        return InventoryServiceWebClient.get()
            .uri("/inventory/stock", uriB -> uriB.queryParam("skuCodes", skuCodes).build())
            .header("Authorization", jwt)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<InventoryResponse>>() {})
            .onErrorMap(e ->  new InventoryConnectionFailureException("Unable to place order at this point of time.",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    Error.INVENTORY_SERVICE_CONNECTION_FAILURE));// Handle errors directly
    }



}
