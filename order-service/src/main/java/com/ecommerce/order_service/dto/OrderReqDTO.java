package com.ecommerce.order_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record OrderReqDTO(
        @NotEmpty(message = "La orden debe contener al menos un item")
        @Valid
        List<OrderLineItemsReqDTO> orderLineItemsList,

        @NotBlank(message = "Correo requerido")
        @Email(message = "El correo no es valido")
        String userEmail
) { }
