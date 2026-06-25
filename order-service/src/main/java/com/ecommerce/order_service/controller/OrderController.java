package com.ecommerce.order_service.controller;

import com.ecommerce.order_service.dto.OrderReqDTO;
import com.ecommerce.order_service.dto.OrderRespDTO;
import com.ecommerce.order_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderRespDTO createOrder(@Valid @RequestBody OrderReqDTO orderReqDTO, @AuthenticationPrincipal Jwt jwt) {
        return orderService.placeOrder(orderReqDTO, jwt.getSubject());
    }

    @GetMapping("byUser")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderRespDTO> getOrdersByUser(@AuthenticationPrincipal Jwt jwt) {
        return   orderService.getOrdersByUserId(jwt.getSubject());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<OrderRespDTO> getAllOrders(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        boolean isAdmin = false;

        Map<String, Object> realmAcccess = jwt.getClaim("realm_access");

        if(realmAcccess!=null && realmAcccess.containsKey("roles")){
            List<String> roles = (List<String>) realmAcccess.get("roles");
            isAdmin = roles.stream().anyMatch(role -> role.equalsIgnoreCase("ADMIN"));
        }
        return   orderService.getAllOrders(isAdmin);
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
