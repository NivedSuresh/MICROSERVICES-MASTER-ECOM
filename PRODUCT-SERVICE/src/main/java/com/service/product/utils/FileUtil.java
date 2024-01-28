package com.service.product.utils;

import com.service.product.payloads.ProductCreateRequest;

import java.util.List;

public interface FileUtil {
    List<String> uploadImagesAndGetURL(ProductCreateRequest request, int imagesAllowed);

    void deleteImageFromFile(String imagesUrl);
}
