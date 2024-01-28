package com.service.inventory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.inventory.model.Inventory;
import com.service.inventory.payloads.InventoryRequest;
import com.service.inventory.repo.InventoryRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class InventoryServiceApplicationTests {

    @Container
    private final static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    static {
        postgres.start();
    }

    @Autowired
    InventoryRepo inventoryRepo;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;


    @Test
    void updateStock() throws Exception {
        InventoryRequest request = new InventoryRequest("65b402901040723ccbaab468", 110);
        mockMvc.perform(MockMvcRequestBuilders.post("/update")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());
        Inventory inventory = inventoryRepo.findBySkuCode("65b402901040723ccbaab468");
        Assertions.assertEquals(inventory.getQuantity(), 110);
    }

    @Test
    void testInStock() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/stock")
               .param("skuCodes", "65b402901040723ccbaab468", "65b402eb1040723ccbaab469"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.[*].quantity").value(hasSize(2)))  // Adjust JSONPath
               .andExpect(jsonPath("$.[0].quantity").value(10))
               .andExpect(jsonPath("$.[1].quantity").value(110));
    }
}
