package com.ecommerce.product_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductReqDTO(
        @NotBlank(message = "No puede estar vacio")
        String name,
        String description,

        @NotNull(message = "Obligatorio")
        @Positive(message = "Debe ser mayor a 0")
        BigDecimal price
) { }
