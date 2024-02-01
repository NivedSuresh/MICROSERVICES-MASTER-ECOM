package com.service.product.controllers;


import com.service.product.payloads.ProductRequest;
import com.service.product.payloads.ProductResponse;
import com.service.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping(value = "/create", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@ModelAttribute ProductRequest request,
                                         @RequestHeader("Authorization") String jwt){
        return productService.saveOrUpdateProduct(request, jwt);
    }

    @GetMapping("/get/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts(){
        return productService.getAllProducts();
    }

}
