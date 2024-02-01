package com.service.product.payloads;

import com.service.product.model.Product;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
public class ProductRequest {
    private String id;
    @Size(min = 7, message = "Name should be minimum 7 characters.")
    private String name;
    @Size(max = 200, message = "Maximum 200 characters allowed.")
    private String description;
    @Min(value = 0, message = "Price shouldn't be a negative value.")
    private Integer price;
    @Size(min = 2, max = Product.MAX_IMAGES, message = "Two - Six images per product is allowed.")
    private List<MultipartFile> images;
    private Integer quantity;
}
