package com.service.product.service;

import com.service.product.model.Product;
import com.service.product.payloads.ProductCreationRequest;
import com.service.product.payloads.ProductResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductService {

    Mono<List<Product>> getAllProducts();
    void deleteImagesFromStorage(List<String> imagesUrl);

    Mono<ProductResponse> save(ProductCreationRequest request, String jwt);
}
