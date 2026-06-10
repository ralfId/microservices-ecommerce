package com.ecommerce.product_service.dto;

import java.math.BigDecimal;

public record ProductResDTO(
        String id,
        String name,
        String description,
        BigDecimal price
) { }
