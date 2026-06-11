package com.ecommerce.order_service.dto;

import java.math.BigDecimal;

public record OrderLineItemsRespDTO(
        Long id, // AQUÍ SÍ VA EL ID
        String sku,
        BigDecimal price,
        Integer quantity
) { }
