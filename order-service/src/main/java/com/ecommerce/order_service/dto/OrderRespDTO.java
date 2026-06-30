package com.ecommerce.order_service.dto;

import com.ecommerce.order_service.model.OrderStatus;

import java.util.List;

public record OrderRespDTO(
        Long id,
        String orderNumber,
        OrderStatus status,
        List<OrderLineItemsRespDTO> orderLineItemsList
) { }
