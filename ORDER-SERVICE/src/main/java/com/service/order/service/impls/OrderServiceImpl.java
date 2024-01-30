package com.service.order.service.impls;

import com.service.order.exception.Error;
import com.service.order.exception.LackOfInformationFromDbException;
import com.service.order.exception.OutOfStockException;
import com.service.order.exception.UnableToPlaceOrderException;
import com.service.order.mapper.Mapper;
import com.service.order.models.Order;
import com.service.order.payloads.InventoryResponse;
import com.service.order.payloads.OrderRequest;
import com.service.order.payloads.OrderDto;
import com.service.order.repo.OrderRepo;
import com.service.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepo orderRepo;
    private final Mapper mapper;
    private final WebClient InventoryServiceWebClient;

    public OrderServiceImpl(OrderRepo orderRepo, Mapper mapper, WebClient.Builder builder) {
        this.orderRepo = orderRepo;
        this.mapper = mapper;
        this.InventoryServiceWebClient = builder
                .baseUrl("http://api-gateway/api")
                .build();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public OrderDto createOrder(OrderRequest request) {

        log.info("Received OrderRequest : {}", request);

        Map<String, Integer> requiredStockInfo =
                mapper.getRequiredStockInfo(request.getOrderItemsDtoList());
        List<InventoryResponse> availableStockInfo =
                getAvailableStockInfoFromInventory(requiredStockInfo.keySet());

        /* Will throw an exception if there aren't enough
        products in quantity for the specified SkuCode or if the
        Request is not valid, ie -> purchase quantity should be >= 1 */
        verifyRequestAndStock(availableStockInfo, requiredStockInfo);
        log.info("All products have desired amount of quantity.");


        Double totalPrice = mapper.getOrderTotalPrice(request.getOrderItemsDtoList());
        if(totalPrice == null) throw new UnableToPlaceOrderException(
                "Malfunction!",
                HttpStatus.BAD_REQUEST.value(),
                Error.ORDER_ITEM_LIST_PRICE_EMPTY
        );

        Order order = new Order();
        order.setOrderItems(mapper.toOrderItemEntity(request.getOrderItemsDtoList(), order));
        order.setTotalPrice(totalPrice);
        order = orderRepo.save(order);


        log.info("Order saved with ID : {}", order.getId());
        return mapper.orderEntityToDto(order);
    }

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
    }


    public List<InventoryResponse> getAvailableStockInfoFromInventory(Set<String> skuCodes) {
        try{
            return InventoryServiceWebClient.get()
                    .uri("/inventory/stock",
                            uriB -> uriB.queryParam("skuCodes", skuCodes).build()
                    )
                    .retrieve()
                    .bodyToFlux(InventoryResponse.class).collectList().block();
        }catch (Exception e){
            e.printStackTrace();
            throw new UnableToPlaceOrderException("Unable to place order at this point of time.",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    Error.INVENTORY_SERVICE_CONNECTION_FAILURE
                    );
        }
    }
}
