package com.service.product.utils;

import com.service.product.payloads.ProductRequest;

import java.util.List;

public interface FileUtil {
    List<String> uploadImagesAndGetURL(ProductRequest request, int imagesAllowed);

    void deleteImageFromFile(String imagesUrl);
}
