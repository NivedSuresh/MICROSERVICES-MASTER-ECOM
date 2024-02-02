package com.service.product.utils;

import com.service.product.payloads.ProductCreationRequest;

import java.util.List;

public interface FileUtil {
    List<String> uploadImagesAndGetURL(ProductCreationRequest request, int imagesAllowed);

    void deleteImageFromFile(String imagesUrl);
}
