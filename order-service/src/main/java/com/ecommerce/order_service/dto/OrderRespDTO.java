package com.ecommerce.order_service.dto;

import java.util.List;

public record OrderRespDTO(
        Long id,
        String orderNumber,
        List<OrderLineItemsRespDTO> orderLineItemsList
) { }
