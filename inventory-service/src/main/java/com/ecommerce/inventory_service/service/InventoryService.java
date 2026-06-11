package com.ecommerce.inventory_service.service;

import com.ecommerce.inventory_service.dto.InventoryReqDTO;
import com.ecommerce.inventory_service.dto.InventoryResDTO;

import java.util.List;

public interface InventoryService {
    boolean isInStock(String sku, Integer quantity);
    InventoryResDTO createInventory(InventoryReqDTO inventoryReqDTO);
    List<InventoryResDTO> getAllInventory();
    InventoryResDTO updateInventory(Long id, InventoryReqDTO inventoryReqDTO);
    void deleteInventory(Long id);
}
