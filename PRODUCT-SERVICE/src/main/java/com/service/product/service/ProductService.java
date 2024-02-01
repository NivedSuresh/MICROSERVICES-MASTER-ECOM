package com.service.product.service;

import com.service.product.payloads.ProductRequest;
import com.service.product.payloads.ProductResponse;

import java.util.List;

public interface ProductService {

    ProductResponse saveOrUpdateProduct(ProductRequest request, String jwt);

    List<ProductResponse> getAllProducts();
    void deleteImagesFromStorage(List<String> imagesUrl);
}
