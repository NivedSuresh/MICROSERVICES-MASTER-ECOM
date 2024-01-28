package com.service.inventory.mapper;

import com.service.inventory.exceptions.Error;
import com.service.inventory.exceptions.InvalidDataReturnedByDb;
import com.service.inventory.payloads.InventoryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InventoryMapper implements Mapper{
    @Override
    public List<InventoryResponse> stockInfoListToInventoryResponseList(List<Object[]> stockInfo) {
        try{
            return stockInfo.stream().map(edges -> new InventoryResponse(
                    (String) edges[0], (Integer) edges[1])).collect(Collectors.toList());
        }catch (Exception e){
            log.error("Failure while Mapping StockInfo to List<InventoryResponse>, : {}", e.getMessage());
            throw new InvalidDataReturnedByDb(
                    "Data transition failure.",
                    Error.INVALID_DATA_RETURNED_FROM_DB, HttpStatus.PARTIAL_CONTENT.value()
            );
        }
    }
}
