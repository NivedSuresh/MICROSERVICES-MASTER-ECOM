package com.service.product.controllers;


import com.service.product.exception.Error;
import com.service.product.model.Product;
import com.service.product.payloads.ErrorResponse;
import com.service.product.payloads.ProductCreationRequest;
import com.service.product.payloads.ProductResponse;
import com.service.product.service.ProductService;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final io.github.resilience4j.circuitbreaker.CircuitBreaker circuitBreaker;

    @PostMapping(value = "/create", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "inventory", fallbackMethod = "createProductFallback")
    @TimeLimiter(name = "inventory", fallbackMethod = "createProductFallback")
    public Mono<ProductResponse> createProduct(@ModelAttribute ProductCreationRequest request,
                                               @RequestHeader("Authorization") String jwt){
        return productService.save(request, jwt)
                .transform(CircuitBreakerOperator.of(circuitBreaker));
    }

    public Mono<ResponseEntity<ErrorResponse>> createProductFallback(ProductCreationRequest request, String jwt, Exception e){
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
                                .message("This service is unavailable at the moment please try again later!")
                                .code(Error.INVENTORY_SERVICE_RESPONDED_WITH_ERROR_STATUS)
                                .build()
                ));
    }

    @GetMapping("/get/all")
    @ResponseStatus(HttpStatus.OK)
    public Mono<List<Product>> getAllProducts(){
        return productService.getAllProducts();
    }

}
