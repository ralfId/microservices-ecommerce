package com.ecommerce.order_service.controller;

import com.ecommerce.order_service.dto.OrderReqDTO;
import com.ecommerce.order_service.dto.OrderRespDTO;
import com.ecommerce.order_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderRespDTO createOrder(@Valid @RequestBody OrderReqDTO orderReqDTO) {
        return orderService.placeOrder(orderReqDTO);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<OrderRespDTO> getAllOrders() {
        return   orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public OrderRespDTO getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrderById(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }

}
