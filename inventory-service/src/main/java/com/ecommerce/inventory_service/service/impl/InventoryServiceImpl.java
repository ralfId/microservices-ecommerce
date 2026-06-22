package com.ecommerce.inventory_service.service.impl;

import com.ecommerce.inventory_service.dto.InventoryReqDTO;
import com.ecommerce.inventory_service.dto.InventoryResDTO;
import com.ecommerce.inventory_service.exception.ResourceNotFoundException;
import com.ecommerce.inventory_service.mapper.InventoryMapper;
import com.ecommerce.inventory_service.model.Inventory;
import com.ecommerce.inventory_service.repository.InventoryRepository;
import com.ecommerce.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@RefreshScope
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;

    @Value("${inventory.allow-backorders:false}")
    private boolean allowBackorders;


    @Override
    @Transactional(readOnly = true)
    public boolean isInStock(String sku, Integer quantity) {

        if (allowBackorders) {
            log.info("MODO BACKORDER ACTIVO: Autorizando stock para SKU: {}", sku);
            return true;
        }

        return inventoryRepository.findBySku(sku)
                .map(inv -> inv.getQuantity() >= quantity)
                .orElse(false);
    }

    @Override
    @Transactional
    public InventoryResDTO createInventory(InventoryReqDTO inventoryReqDTO) {

        boolean exists = inventoryRepository.existsBySku(inventoryReqDTO.sku());
        if (exists) {
            throw new RuntimeException("Ya existe registro para el SKU: " + inventoryReqDTO.sku());
        }

        Inventory newInventory = inventoryMapper.reqDtoToInventory(inventoryReqDTO);
        Inventory savedInventory = inventoryRepository.save(newInventory);

        log.info("Inventario creado, SKU: {}", savedInventory.getSku());

        return inventoryMapper.inventoryToResDto(savedInventory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResDTO> getAllInventory() {
        return inventoryRepository.findAll()
                .stream()
                .map(inventoryMapper::inventoryToResDto)
                .toList();
    }

    @Override
    @Transactional
    public InventoryResDTO updateInventory(Long id, InventoryReqDTO inventoryReqDTO) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Inventory", "id", id)
                );

        inventory.setSku(inventoryReqDTO.sku());
        inventory.setQuantity(inventoryReqDTO.quantity());

        Inventory updatedInventory = inventoryRepository.save(inventory);

        log.info("Inventario actualizado, id: {}", updatedInventory.getId());

        return inventoryMapper.inventoryToResDto(updatedInventory);
    }

    @Override
    @Transactional
    public void deleteInventory(Long id) {
        if (!inventoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inventory", "id", id);
        }
        inventoryRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void reduceStock(String sku, Integer quantity) {
        Inventory inventory = inventoryRepository.findBySku(sku)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Inventory", "sku", sku)
                );

        if (inventory.getQuantity() < quantity) {
            throw new RuntimeException("Stock Insuficiente para: " + sku);
        }

        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventoryRepository.save(inventory);
    }
}
