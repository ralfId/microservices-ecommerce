package com.ecommerce.notification_service.listener;

import com.ecommerce.notification_service.event.OrderCancelledEvent;
import com.ecommerce.notification_service.event.OrderConfirmedEvent;
import com.ecommerce.notification_service.event.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
@RabbitListener(queues = "notification-queue")
@Slf4j
public class OrderEventsListener {

    private final JavaMailSender javaMailSender;

    @RabbitHandler
    public void handleOrderConfirmedEvent(OrderConfirmedEvent orderPlacedEvent) {
        log.info("Evento recibido en inventario para la order: {}", orderPlacedEvent.orderNumber());

        try{
            log.info("Enviando correo de confirmacion a: {}", orderPlacedEvent.email());

            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedDate = today.format(formatter);

            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom("pedidos@ecommerce.com");
            message.setTo(orderPlacedEvent.email());
            message.setSubject("Order Confirmada - " +  orderPlacedEvent.orderNumber());

            String messageContent = """
                    ¡Hola!
                    
                    Tu pedido con numero %s ha sido recibido exitosamente.
                    Pronto recibiras mas noticias sobre el envio.
                    
                    Gracias por comprar con nosotros.
                    
                    PEDIDOS-ECOMMERCE %s
                    """.formatted(orderPlacedEvent.orderNumber(), formattedDate);

            message.setText(messageContent);

            javaMailSender.send(message);

            log.info("Correo enviado con exito para la orden: {}", orderPlacedEvent.orderNumber());
        } catch (Exception e) {
            log.error("Error al enviar correo para la orden {}: {} ", orderPlacedEvent.orderNumber(), e.getMessage());
        }
    }


    @RabbitHandler
    public void handleOrderCancelledEvent(OrderCancelledEvent order) {
        log.info("Enviando correo de cancelacion para la orden: {}", order.orderNumber());

        try{
            log.info("Enviando correo de cancelacion a: {}", order.email());

            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedDate = today.format(formatter);

            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom("pedidos@ecommerce.com");
            message.setTo(order.email());
            message.setSubject("Orden Cancelada - " +  order.orderNumber());

            String messageContent = """
                    ¡Hola!
                    
                    Tu pedido con numero %s ha sido CANCELADO.
                    MOTIVO: %s
                    
                    Si se realizo algun cargo sera reembolzado a la brevedad.
                    
                    PEDIDOS-ECOMMERCE %s
                    """.formatted(order.orderNumber(),order.reason(), formattedDate);

            message.setText(messageContent);

            javaMailSender.send(message);

            log.info("Correo enviado con exito para la orden cancelada: {}", order.orderNumber());
        } catch (Exception e) {
            log.error("Error al enviar correo para la orden cancelada {}: {} ", order.orderNumber(), e.getMessage());
        }
    }
}
