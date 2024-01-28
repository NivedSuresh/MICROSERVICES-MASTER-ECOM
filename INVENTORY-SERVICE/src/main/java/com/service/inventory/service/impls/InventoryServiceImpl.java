package com.service.inventory.service.impls;

import com.service.inventory.exceptions.Error;
import com.service.inventory.exceptions.InvalidDataReturnedByDb;
import com.service.inventory.exceptions.InventoryException;
import com.service.inventory.mapper.Mapper;
import com.service.inventory.model.Inventory;
import com.service.inventory.payloads.InventoryRequest;
import com.service.inventory.payloads.InventoryResponse;
import com.service.inventory.repo.InventoryRepo;
import com.service.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepo inventoryRepo;
    private final Mapper mapper;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<InventoryResponse> getInventoryStock(List<String> skuCodes) {
        try{
            List<Object[]> stockInfo = inventoryRepo.findQuantitiesBySkuCode(skuCodes);
            log.info("Total number of stocks fetched, a list of size {} was returned.", stockInfo.size());

            return mapper.stockInfoListToInventoryResponseList(stockInfo);
        }catch (Exception e){
            log.error("Exception caught, CAUSE : {}", e.getMessage());
            if (e instanceof InventoryException) throw e;

            throw new InvalidDataReturnedByDb(
                    "Exception occurred when fetching data from database",
                    Error.INVALID_DATA_RETURNED_FROM_DB, HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
        }

    }

    @Override
    public void updateInventory(InventoryRequest inventoryRequest) {
        try{
//            throw new RuntimeException();
            Inventory inventory = inventoryRepo.findBySkuCode(inventoryRequest.getSkuCode());
            if(inventory == null) inventory = Inventory.builder()
                    .skuCode(inventoryRequest.getSkuCode())
                    .build();
            inventory.setQuantity(inventoryRequest.getQuantity());
            inventoryRepo.save(inventory);
        }catch (Exception e){
            throw new InventoryException(
                    "Unable to save inventory specific data",
                    Error.DATABASE_OPERATION_FAILURE,
                    HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
        }
    }
}
