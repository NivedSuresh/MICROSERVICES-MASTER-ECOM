package com.service.product.utils.impls;

import com.service.product.payloads.ProductCreationRequest;
import com.service.product.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class FileUtilImpl implements FileUtil {

    private static final String IMAGE_UPLOAD_FOLDER = "/Users/apple/Documents/Projects/E-COM/PRODUCT-SERVICE/src/main/resources/static/images";
    @Override
    public List<String> uploadImagesAndGetURL(ProductCreationRequest request, int imagesAllowed) {

        if(imagesAllowed == 0) throw new RuntimeException("This product has already reached it's maximum image count.");

        File uploadDir = new File(IMAGE_UPLOAD_FOLDER);
        if(!uploadDir.exists()) uploadDir.mkdirs();

        List<String> imagesUrl = new ArrayList<>();

        for(MultipartFile file : request.getImages()){
            if(imagesAllowed == 0) return imagesUrl;

            String fileExtension = null;
            try{ fileExtension = getFileExtension(file); }
            catch (Exception e){
                log.error("Couldn't extract file extension for the image {}. CAUSE : {}", file.getName(), e.getMessage());
                continue;
            }
            String fileName = UUID.randomUUID().toString().concat(fileExtension);
            Path destination = Path.of(IMAGE_UPLOAD_FOLDER, fileName);

            while (Files.exists(destination)){
                fileName = UUID.randomUUID().toString().concat(fileExtension);
                destination = Path.of(IMAGE_UPLOAD_FOLDER, fileName);
            }

            try {
                Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
                imagesUrl.add(fileName);
                imagesAllowed--;
            } catch (IOException e) {
                log.error("Couldn't upload the image {} to the designated storage", file.getName());
            }
        }

        return imagesUrl;
    }

    private static String getFileExtension(MultipartFile file) {
        String contentType = file.getContentType();
        if(!Objects.equals(contentType, "image/png") && !Objects.equals(contentType, "image/jpg")
                && !Objects.equals(contentType, "image/jpeg"))
            throw new RuntimeException("Invalid content type, cannot accept file of type "
                    .concat(contentType!=null ? contentType : "empty"));

        return ".".concat(contentType.substring(file.getContentType().lastIndexOf('/')+1));
    }

    public void deleteImageFromFile(String imagesUrl){
        new File(IMAGE_UPLOAD_FOLDER + "/" + imagesUrl).delete();
    }

}
