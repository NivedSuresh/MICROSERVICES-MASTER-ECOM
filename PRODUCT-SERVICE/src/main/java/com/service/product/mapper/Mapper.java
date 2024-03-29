package com.service.product.mapper;

import com.service.product.model.Product;
import com.service.product.payloads.InventoryRequest;
import com.service.product.payloads.ProductCreationRequest;
import com.service.product.payloads.ProductResponse;

public interface Mapper {
    ProductResponse entityToResponse(Product product);
    Product requestToEntity(ProductCreationRequest request);
    InventoryRequest entityToInventoryRequest(String id, Integer stock);
}
