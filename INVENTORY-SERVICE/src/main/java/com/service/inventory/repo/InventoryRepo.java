package com.service.inventory.repo;

import com.service.inventory.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface InventoryRepo extends JpaRepository<Inventory, Long> {
    @Query("select i.quantity from Inventory as i where i.skuCode = :skuCode")
    Integer findQuantityBySkuCode(String skuCode);

    @Query("select i.skuCode, i.quantity from Inventory as i where i.skuCode in :skuCodes")
    List<Object[]> findQuantitiesBySkuCode(List<String> skuCodes);
    boolean existsBySkuCode(String skuCode);

    Inventory findBySkuCode(String skuCode);
}
