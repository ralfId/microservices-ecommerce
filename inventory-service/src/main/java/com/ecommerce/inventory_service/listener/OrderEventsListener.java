package com.ecommerce.inventory_service.listener;

import com.ecommerce.inventory_service.event.OrderCancelledEvent;
import com.ecommerce.inventory_service.event.OrderPlacedEvent;
import com.ecommerce.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventsListener {

    private final InventoryService inventoryService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "inventory-queue")
    public void handleOrderPlacedEvent(OrderPlacedEvent orderPlacedEvent) {

        log.info("Evento recibido en inventario para la order: {}", orderPlacedEvent.orderNumber());

       try{
           boolean allProductsHasStock = orderPlacedEvent.items().stream().allMatch(item ->
                   inventoryService.isInStock(item.sku(), item.quantity()));

           if (!allProductsHasStock) {
               cancelledOrder(orderPlacedEvent, "Stock insuficiente en uno o mas productos");
               return;
           }

           orderPlacedEvent.items().forEach(item -> {
               inventoryService.reduceStock(item.sku(), item.quantity());
           });

           log.info("Stock descontado para la orden numero {}", orderPlacedEvent.orderNumber());
           rabbitTemplate.convertAndSend("order-events", "order.confirmed", orderPlacedEvent);

       } catch (Exception e) {

           log.error("Error al descontar stock para la orden numero {}: {}", orderPlacedEvent.orderNumber(), e.getMessage());
           cancelledOrder(orderPlacedEvent, "Error tecnico en el procesamiento del inventario");

       }
    }

    private void cancelledOrder(OrderPlacedEvent event, String reason) {
        OrderCancelledEvent cancelledEvent = OrderCancelledEvent.builder()
                .orderNumber(event.orderNumber())
                .email(event.email())
                .reason(reason)
                .build();

        rabbitTemplate.convertAndSend("order-events","order.cancelled", cancelledEvent);
    }
}
