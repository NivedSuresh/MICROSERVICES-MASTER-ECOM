package com.service.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.product.service.ProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.hamcrest.Matchers.hasSize;

@SpringBootTest
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

    @Container
    private final static MongoDBContainer mongo = new MongoDBContainer(DockerImageName.parse("mongo:latest"));
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProductService productService;
    @Autowired
    ObjectMapper objectMapper;



    @DynamicPropertySource
    public static void setProperties(DynamicPropertyRegistry registry){
        mongo.start();
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }

    @Test
    void shouldCreateProduct() throws Exception {
        MockMultipartFile image1 = new MockMultipartFile("images", "image1.jpg", "image/jpeg", "some image data".getBytes());
        MockMultipartFile image2 = new MockMultipartFile("images", "image2.jpg", "image/jpeg", "some image data".getBytes());


        mockMvc.perform(multipart("/create")
                        .file(image1)
                        .file(image2)
                        .param("name", "Product Name")
                        .param("description", "Product Description")
                        .param("totalPrice", "100"))
                .andExpect(status().isCreated());

        Assertions.assertEquals(1, productService.getAllProducts().size());
    }

    @Test
    void shouldGetAllProducts() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/get/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));
    }


}
