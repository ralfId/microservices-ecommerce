package com.ecommerce.order_service.service.impl;

import com.ecommerce.order_service.dto.OrderReqDTO;
import com.ecommerce.order_service.dto.OrderRespDTO;
import com.ecommerce.order_service.exception.ResourceNotFoundException;
import com.ecommerce.order_service.mapper.OrderMapper;
import com.ecommerce.order_service.model.Order;
import com.ecommerce.order_service.repository.OrderRepository;
import com.ecommerce.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final WebClient.Builder webClientBuilder;

    @Override
    @Transactional
    public OrderRespDTO placeOrder(OrderReqDTO orderRequest) {
        log.info("Colocando nueva orden...");

        Order newOrder = orderMapper.toOrder(orderRequest);

        for(var item : newOrder.getOrderLineItemsList()){
            String sku = item.getSku();
            Integer quantity = item.getQuantity();

            Boolean inStock = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8081/api/v1/inventory/" + sku,
                            uriBuilder -> uriBuilder.queryParam("quantity", quantity).build())
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();

            if(!Boolean.TRUE.equals(inStock)){
                throw new IllegalArgumentException("No hay stock disponible para el producto: " + sku);
            }


        }


        newOrder.setOrderNumber(UUID.randomUUID().toString());

        Order savedOrder = orderRepository.save(newOrder);

        log.info("Orden guardada con éxito. ID: {}", savedOrder.getId());

        return orderMapper.toOrderResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderRespDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toOrderResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderRespDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden", "id", id));
        return orderMapper.toOrderResponse(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Orden", "id", id);
        }
        orderRepository.deleteById(id);
        log.info("Orden eliminada. ID: {}", id);
    }
}
