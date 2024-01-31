package com.service.product.service.impls;

import com.service.product.exception.*;
import com.service.product.exception.Error;
import com.service.product.mapper.Mapper;
import com.service.product.model.Product;
import com.service.product.payloads.InventoryRequest;
import com.service.product.payloads.ProductCreateRequest;
import com.service.product.payloads.ProductResponse;
import com.service.product.repo.ProductRepo;
import com.service.product.utils.FileUtil;
import com.service.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepo;
    private final FileUtil fileUtil;
    private final Mapper mapper;
    private final RestClient inventoryRestClient;

    public ProductServiceImpl(ProductRepo productRepo, FileUtil fileUtil, Mapper mapper, RestClient.Builder builder) {
        this.productRepo = productRepo;
        this.fileUtil = fileUtil;
        this.mapper = mapper;
        this.inventoryRestClient = builder
                .baseUrl("http://api-gateway/api")
                .build();
    }

    @Override
    @Transactional()
    public ProductResponse saveOrUpdateProduct(ProductCreateRequest request, String jwt) {
        Product product = mapper.requestToEntity(request);
        try{
            product = productRepo.save(product);
            log.info("Call has been made to inventory service.");

            InventoryRequest inventoryRequest =
                    mapper.entityToInventoryRequest(product.getId(), request.getQuantity());

            HttpStatusCode statusCode = inventoryRestClient.post()
                    .uri("/inventory/update")
                    .header("Authorization", jwt)
                    .body(inventoryRequest)
                    .exchange((req, res)-> res.getStatusCode());

            if(statusCode != HttpStatus.CREATED){
                log.info("Status Code : {}",statusCode);
                throw new InventoryWriteException(
                        "Error occurred while saving the product",
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        Error.INVENTORY_SERVICE_RESPONDED_WITH_ERROR_STATUS
                );
            }
            log.info("Product saved in database with ID : {}", product.getId());
            return mapper.entityToResponse(product);
        }
        catch (Exception e){
            this.deleteImagesFromStorage(product.getImagesUrl());

            log.error("Exception Caught : {}", e.getMessage());

            if(e instanceof ProductException) throw e;
            throw new ProductWriteException("Failed to write product : ".concat(e.getMessage()),
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                Error.ERROR_SAVING_ENTITY);
        }
    }

    @Override
    public void deleteImagesFromStorage(List<String> imagesUrl) {
        for(String imageUrl : imagesUrl) fileUtil.deleteImageFromFile(imageUrl);
    }


    @Override
    public List<ProductResponse> getAllProducts() {
        try{
            List<Product> products = productRepo.findAll();
            return products.stream().map(mapper::entityToResponse).collect(Collectors.toList());
        }catch (Exception e){
            throw new ProductReadException("An Internal error occurred",
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                Error.ERROR_FETCHING_ENTITY);
        }
    }
}
