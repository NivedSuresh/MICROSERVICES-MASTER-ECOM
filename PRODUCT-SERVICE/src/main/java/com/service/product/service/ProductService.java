package com.service.product.service;

import com.service.product.payloads.ProductCreateRequest;
import com.service.product.payloads.ProductResponse;

import java.util.List;

public interface ProductService {

    ProductResponse saveOrUpdateProduct(ProductCreateRequest request, String jwt);

    List<ProductResponse> getAllProducts();
    void deleteImagesFromStorage(List<String> imagesUrl);
}
