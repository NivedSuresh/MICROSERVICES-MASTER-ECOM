package com.service.product.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@Document(value = "products")
public class Product {

    public static final int MAX_IMAGES = 6;

    @Id
    private String id;
    private String name;
    private String description;
    private Integer price;
    private List<String> imagesUrl;

    @Override
    public boolean equals(Object o){
        return (o instanceof Product) && Objects.equals(((Product) o).id, this.id);
    }
}
