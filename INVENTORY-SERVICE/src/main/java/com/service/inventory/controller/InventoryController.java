package com.service.inventory.controller;

import com.service.inventory.payloads.InventoryRequest;
import com.service.inventory.payloads.InventoryResponse;
import com.service.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/stock")
    @ResponseStatus(HttpStatus.OK)
    @SneakyThrows
    public List<InventoryResponse> getStock(@RequestParam List<String> skuCodes){
//        Thread.sleep(6000);
        return inventoryService.getInventoryStock(skuCodes);
    }

    @PostMapping("/update")
    @ResponseStatus(HttpStatus.CREATED)
    public void updateInventory(@RequestBody InventoryRequest inventoryRequest){
        log.info("Call has been received by inventory service");
        inventoryService.updateInventory(inventoryRequest);
    }


}
