package com.ecommerce.inventory_service.dto;

public record InventoryResDTO(
        Long id,
        String sku,
        Integer quantity,
        boolean inStock // campo calculado
) { }
