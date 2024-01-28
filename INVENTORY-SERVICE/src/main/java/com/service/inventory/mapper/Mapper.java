package com.service.inventory.mapper;

import com.service.inventory.payloads.InventoryResponse;

import java.util.List;
import java.util.Map;

public interface Mapper {
    List<InventoryResponse> stockInfoListToInventoryResponseList(List<Object[]> stockInfo);
}
