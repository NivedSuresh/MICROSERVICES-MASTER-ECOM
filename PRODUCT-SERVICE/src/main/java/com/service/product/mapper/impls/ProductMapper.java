package com.service.product.mapper.impls;

import com.service.product.mapper.Mapper;
import com.service.product.model.Product;
import com.service.product.payloads.InventoryRequest;
import com.service.product.payloads.ProductCreateRequest;
import com.service.product.payloads.ProductResponse;
import com.service.product.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductMapper implements Mapper {

    private final FileUtil fileUtil;

    @Override
    public ProductResponse entityToResponse(Product product){
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .imagesUrl(product.getImagesUrl())
                .description(product.getDescription()).build();
    }

    @Override
    public Product requestToEntity(ProductCreateRequest request) {
        return Product.builder()
                .id(request.getId())
                .name(request.getName())
                .price(request.getPrice())
                .imagesUrl(fileUtil.uploadImagesAndGetURL(request, Product.MAX_IMAGES))
                .description(request.getDescription())
                .build();
    }

    @Override
    public InventoryRequest entityToInventoryRequest(String id, Integer stock) {
        return new InventoryRequest(id, stock);
    }

}
