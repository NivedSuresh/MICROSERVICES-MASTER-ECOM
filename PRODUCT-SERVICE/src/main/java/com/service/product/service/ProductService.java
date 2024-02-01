package com.service.product.service;

import com.service.product.model.Product;
import com.service.product.payloads.ProductRequest;
import com.service.product.payloads.ProductResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductService {

    ProductResponse saveOrUpdateProduct(ProductRequest request, String jwt);

    Mono<List<Product>> getAllProducts();
    void deleteImagesFromStorage(List<String> imagesUrl);

    Mono<ProductResponse> save(ProductRequest request, String jwt);
}
