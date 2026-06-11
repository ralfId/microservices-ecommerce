package com.ecommerce.inventory_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record InventoryReqDTO(
        @NotBlank(message = "SKU: No puede estar vacio")
        String sku,
        @Min(value = 0, message = "Cantidad: No puede ser negativa")
        Integer quantity
) { }
