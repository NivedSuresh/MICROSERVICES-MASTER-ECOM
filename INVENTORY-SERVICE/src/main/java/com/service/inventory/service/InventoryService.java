package com.service.inventory.service;

import com.service.inventory.payloads.InventoryRequest;
import com.service.inventory.payloads.InventoryResponse;

import java.util.List;
import java.util.Map;

public interface InventoryService {
    List<InventoryResponse> getInventoryStock(List<String> skuCode);

    void updateInventory(InventoryRequest inventoryRequest);
}
