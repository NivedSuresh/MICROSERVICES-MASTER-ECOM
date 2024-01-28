package com.service.product.payloads;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProductResponse {
    private String id;
    private String name;
    private String description;
    private Integer price;
    private List<String> imagesUrl;
}
