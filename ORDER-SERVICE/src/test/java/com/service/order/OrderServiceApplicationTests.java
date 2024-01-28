package com.service.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.order.payloads.OrderItemDto;
import com.service.order.payloads.OrderRequest;
import com.service.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;


import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderServiceApplicationTests {

    @Autowired
    private MockMvc mvc;
    @Autowired
    OrderService orderService;
    @Autowired
    ObjectMapper objectMapper;

    @Container
    public static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:9.6.12"));


    static {
        postgres.start();
    }

    @Test
    public void createOrder() throws Exception {
        OrderItemDto orderItemDto1 = OrderItemDto.builder()
                .skuCode("65b402eb1040723ccbaab469")
                .quantityPerItem(9)
                .totalPrice(100.0)
                .build();
        OrderItemDto orderItemDto2 = OrderItemDto.builder()
                .skuCode("65b402901040723ccbaab468")
                .quantityPerItem(5)
                .totalPrice(10.0)
                .build();

        OrderRequest orderRequest = OrderRequest.builder()
                        .orderItemsDtoList(List.of(orderItemDto2, orderItemDto1)).build();

        mvc.perform(MockMvcRequestBuilders.post("/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalPrice").value(110))
                .andReturn();

    }


}
