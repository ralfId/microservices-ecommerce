package com.ecommerce.inventory_service.controller;

import com.ecommerce.inventory_service.dto.InventoryReqDTO;
import com.ecommerce.inventory_service.dto.InventoryResDTO;
import com.ecommerce.inventory_service.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
@Slf4j
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{sku}")
    @ResponseStatus(HttpStatus.OK)
    public boolean isInStock(@PathVariable String sku, @RequestParam Integer quantity) {
        return inventoryService.isInStock(sku, quantity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryResDTO addInventory(@Valid @RequestBody InventoryReqDTO inventoryReqDTO) {
        return  inventoryService.createInventory(inventoryReqDTO);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResDTO> getAllInventory() {
        return inventoryService.getAllInventory();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public InventoryResDTO updateInventory(@PathVariable Long id, @Valid @RequestBody InventoryReqDTO inventoryReqDTO) {
        return inventoryService.updateInventory(id, inventoryReqDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInventoryById(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
    }

    @PutMapping("/reduce/{sku}")
    @ResponseStatus(HttpStatus.OK)
    public String reduceStock(@PathVariable String sku, @RequestParam Integer quantity) {
        inventoryService.reduceStock(sku, quantity);
        return "Stock Reducido";
    }
}
