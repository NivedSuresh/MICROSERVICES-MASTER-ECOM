package com.service.product.service.impls;

import com.service.product.exception.*;
import com.service.product.exception.Error;
import com.service.product.mapper.Mapper;
import com.service.product.model.Product;
import com.service.product.payloads.InventoryRequest;
import com.service.product.payloads.ProductRequest;
import com.service.product.payloads.ProductResponse;
import com.service.product.repo.ProductRepo;
import com.service.product.utils.FileUtil;
import com.service.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepo;
    private final FileUtil fileUtil;
    private final Mapper mapper;
    private final WebClient webClient;

    public ProductServiceImpl(ProductRepo productRepo, FileUtil fileUtil, Mapper mapper, WebClient.Builder builder) {
        this.productRepo = productRepo;
        this.fileUtil = fileUtil;
        this.mapper = mapper;
        this.webClient = builder
                .baseUrl("http://api-gateway/api")
                .build();
    }

    @Override
    @Transactional()
    public ProductResponse saveOrUpdateProduct(ProductRequest request, String jwt) {
        Product product = mapper.requestToEntity(request);
        try{
            product = productRepo.save(product);
            log.info("Call has been made to inventory service.");

            writeInventoryAndValidateHttpStatus(request, jwt, product);
            return mapper.entityToResponse(product);
        }
        catch (Exception e){

            log.error("Exception Caught : {}", e.getMessage());

            if(e instanceof ProductException) throw e;
            throw new ProductWriteException("Failed to write product : ".concat(e.getMessage()),
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                Error.ERROR_SAVING_ENTITY);
        }
    }


    public Mono<ProductResponse> save(ProductRequest request, String jwt){

        return Mono.just(mapper.requestToEntity(request))

                .publishOn(Schedulers.boundedElastic())
                .handle((product, synchronousSink) -> {
                    product = productRepo.save(product);
                    synchronousSink.next(product);
                })

                .zipWhen(o ->
                        writeInventoryAndValidateHttpStatus(request, jwt, (Product) o)
                )

                .map(objects -> {
                    Product product = (Product)objects.get(0);
                    return mapper.entityToResponse(product);
                });
    }

    private Mono<ResponseEntity<Void>> writeInventoryAndValidateHttpStatus(ProductRequest request, String jwt, Product product) {
        InventoryRequest inventoryRequest =
                mapper.entityToInventoryRequest(product.getId(), request.getQuantity());

        return webClient.post()
                .uri("/inventory/update")
                .header("Authorization", jwt)
                .body(BodyInserters.fromValue(inventoryRequest))
                .exchangeToMono(ClientResponse::toBodilessEntity);

    }

    private void validateStatusCode(HttpStatusCode statusCode, Product product){
        if(statusCode != HttpStatus.CREATED){
            log.info("Status Code : {}",statusCode);
            deleteImagesFromStorage(product.getImagesUrl());
            throw new InventoryWriteException(
                    "Error occurred while saving the product",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    Error.INVENTORY_SERVICE_RESPONDED_WITH_ERROR_STATUS
            );
        }
    }



    @Override
    public void deleteImagesFromStorage(List<String> imagesUrl) {
        for(String imageUrl : imagesUrl) fileUtil.deleteImageFromFile(imageUrl);
    }


    @Override
    public Mono<List<Product>> getAllProducts() {
        try{
            return Mono.just(productRepo.findAll());
        }catch (Exception e){
            throw new ProductReadException("An Internal error occurred",
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                Error.ERROR_FETCHING_ENTITY);
        }
    }
}
