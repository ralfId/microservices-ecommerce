package com.ecommerce.order_service.service;

import com.ecommerce.order_service.dto.OrderReqDTO;
import com.ecommerce.order_service.dto.OrderRespDTO;

import java.util.List;

public interface OrderService {
    OrderRespDTO placeOrder(OrderReqDTO orderRequest);   // Create
    List<OrderRespDTO> getAllOrders();                   // Read All
    OrderRespDTO getOrderById(Long id);                  // Read One
    void deleteOrder(Long id);                           // Delete
}
