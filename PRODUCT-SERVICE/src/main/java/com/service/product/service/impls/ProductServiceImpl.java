package com.service.product.service.impls;

import com.service.product.events.NotificationEvent;
import com.service.product.exception.*;
import com.service.product.exception.Error;
import com.service.product.mapper.Mapper;
import com.service.product.model.Product;
import com.service.product.payloads.InventoryRequest;
import com.service.product.payloads.ProductCreationRequest;
import com.service.product.payloads.ProductResponse;
import com.service.product.repo.ProductRepo;
import com.service.product.utils.FileUtil;
import com.service.product.service.ProductService;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.concurrent.ExecutorService;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepo;
    private final FileUtil fileUtil;
    private final Mapper mapper;
    private final WebClient webClient;
    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public ProductServiceImpl(ProductRepo productRepo, FileUtil fileUtil, Mapper mapper, WebClient.Builder builder, KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
        this.productRepo = productRepo;
        this.fileUtil = fileUtil;
        this.mapper = mapper;
        this.webClient = builder
                .baseUrl("http://api-gateway/api")
                .build();
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    @Override
    public Mono<ProductResponse> save(ProductCreationRequest request, String jwt){

        return Mono.just(mapper.requestToEntity(request))

                .publishOn(Schedulers.boundedElastic())
                .map(productRepo::save)

                .zipWhen(product -> writeInventoryAndValidateHttpStatus(request, jwt, product))

                .map(objects -> {
                    validateStatusCode(objects.getT2().getStatusCode(), objects.getT1());
                    createKafkaEventForProductAddition(objects.getT1() , jwt);
                    return mapper.entityToResponse(objects.getT1());
                });
    }

    private void createKafkaEventForProductAddition(Product product, String jwt) {
        kafkaTemplate.send("notification", NotificationEvent.builder()
                .email(jwt) //extract email from jwt
                .notification(draftEmailForProductAddition(product)).build());
    }

    private String draftEmailForProductAddition(Product product) {
        return new StringBuilder("A new Product has been added to the website with name '")
                .append(product.getName()).append("'. The price for the product is ")
                .append(product.getPrice()).append("!").toString();
    }

    @Retry(name = "inventory")
    private Mono<ResponseEntity<Void>> writeInventoryAndValidateHttpStatus(ProductCreationRequest request, String jwt, Product product) {
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
