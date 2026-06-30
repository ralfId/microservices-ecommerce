package com.ecommerce.order_service.service.impl;

import com.ecommerce.order_service.dto.OrderReqDTO;
import com.ecommerce.order_service.dto.OrderRespDTO;
import com.ecommerce.order_service.event.OrderPlacedEvent;
import com.ecommerce.order_service.exception.ResourceNotFoundException;
import com.ecommerce.order_service.mapper.OrderMapper;
import com.ecommerce.order_service.model.Order;
import com.ecommerce.order_service.model.OrderStatus;
import com.ecommerce.order_service.repository.OrderRepository;
import com.ecommerce.order_service.service.OrderService;
import com.ecommerce.order_service.service.client.InventoryClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@RefreshScope
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
//    private final WebClient.Builder webClientBuilder;
//    private final InventoryClient inventoryClient;
    private final RabbitTemplate rabbitTemplate;

    @Value("${order.enabled:true}")
    private boolean ordersEnabled;

    public static final String EXCHANGE_NAME = "order-events";
    public static final String ROUTING_KEY_EVENT = "order.placed";



    public OrderRespDTO fallbackMethod(OrderReqDTO orderRequest, String userId, Throwable throwable) {
            log.error("Circuit breaker activo. Causa: {}", throwable.getMessage());
            throw new RuntimeException("El servicio de ordenes no responde, intente mas tarde");
    }

    @Override
    @Transactional
//    ### Se deshabilita porque se solventa usando una Arquitectura Orientada a Eventos - RabbitMQ ###
//    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
//    @Retry(name = "inventory")
//    @TimeLimiter(name = "inventory")
     public OrderRespDTO placeOrder(OrderReqDTO orderRequest, String userId) {

           if (!ordersEnabled) {
               log.warn("Pedido Rechazado: Servicio deshabilitado por configuracion");
               throw new RuntimeException("Servicio en mantenimiento, intente mas tarde");
           }


           log.info("Colocando nueva orden...");

           Order newOrder = orderMapper.toOrder(orderRequest);
           newOrder.setUserId(userId);

//           for(var item : newOrder.getOrderLineItemsList()){
//               String sku = item.getSku();
//               Integer quantity = item.getQuantity();
//
//               try {
////                webClientBuilder.build()
////                        .put()
////                        .uri("http://localhost:8081/api/v1/inventory/reduce/" + sku,
////                                uriBuilder -> uriBuilder.queryParam("quantity", quantity).build())
////                        .retrieve()
////                        .bodyToMono(String.class)
////                        .block();
//
//                   inventoryClient.reduceStock(sku, quantity);
//
//
//               }catch (Exception e){
//                   log.error("Error al reducir stock para producto con sku {}: {}", sku, e.getMessage());
//                   throw new IllegalArgumentException("No se pudo procesar la orden: Stock insuficiente o error de inventario");
//               }
//           }


           newOrder.setOrderNumber(UUID.randomUUID().toString());
            newOrder.setStatus(OrderStatus.PLACED);
           Order savedOrder = orderRepository.save(newOrder);



           log.info("Orden guardada con éxito. ID: {}", savedOrder.getId());

           List<OrderPlacedEvent.OrderItemEvent> orderItemEventList =
                   newOrder.getOrderLineItemsList().stream()
                           .map(item->
                                   new OrderPlacedEvent.OrderItemEvent(
                                           item.getSku(),
                                           item.getPrice().toString(),
                                           item.getQuantity())
                           ).toList();

           OrderPlacedEvent orderPlacedEvent =
                   new OrderPlacedEvent(savedOrder.getOrderNumber(), orderRequest.userEmail(), orderItemEventList);

           rabbitTemplate.convertAndSend(EXCHANGE_NAME,  ROUTING_KEY_EVENT, orderPlacedEvent);

           log.info("Evento enviado a RabbitMQ, para la orden: {}", savedOrder.getOrderNumber());

           return orderMapper.toOrderResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderRespDTO> getAllOrders(boolean isAdmin) {
        if(!isAdmin){
            log.info("No es usuario administrador");
            throw new IllegalArgumentException("No es usuario administrador");
        }

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

    @Override
    @Transactional(readOnly = true)
    public List<OrderRespDTO> getOrdersByUserId(String userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(orderMapper::toOrderResponse)
                .toList();
    }

    @Override
    @Transactional
    public void updateOrderStatus(String orderNumber, OrderStatus orderStatus) {
        orderRepository.findByOrderNumber(orderNumber).ifPresentOrElse( order -> {
            order.setStatus(orderStatus);
            orderRepository.save(order);
            log.info("Orden actualizado. ID: {}. Numero Orden: {}.", order.getId(),  order.getOrderNumber());
        }, () -> log.error("No se encontro la orden {} para actualizar", orderNumber));
    }
}
