package com.example.bookstore.dto;

import com.example.bookstore.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    
    private Long id;
    private Long userId;
    private String userName;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private String status;
    private String shippingAddress;
    private String paymentMethod;
    private String paymentId;
    private List<OrderItemDto> items;
    
    public static OrderDto fromEntity(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .userName(order.getUser().getName())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus() != null ? order.getStatus().name() : null)
                .shippingAddress(order.getShippingAddress())
                .paymentMethod(order.getPaymentMethod())
                .paymentId(order.getPaymentId())
                .items(order.getItems().stream()
                        .map(OrderItemDto::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }
    
    public Order toEntity() {
        Order order = new Order();
        order.setId(this.id);
        order.setOrderDate(this.orderDate);
        order.setTotalAmount(this.totalAmount);
        order.setShippingAddress(this.shippingAddress);
        order.setPaymentMethod(this.paymentMethod);
        order.setPaymentId(this.paymentId);
        
        if (this.status != null) {
            try {
                order.setStatus(Order.OrderStatus.valueOf(this.status));
            } catch (IllegalArgumentException e) {
                // Default to PENDING if invalid
                order.setStatus(Order.OrderStatus.PENDING);
            }
        }
        
        return order;
    }
}
