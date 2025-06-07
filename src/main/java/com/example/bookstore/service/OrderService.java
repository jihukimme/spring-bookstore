package com.example.bookstore.service;

import com.example.bookstore.dto.OrderDto;
import com.example.bookstore.dto.OrderItemDto;
import com.example.bookstore.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    
    Page<OrderDto> findAllOrders(Pageable pageable);
    
    OrderDto findById(Long id);
    
    Page<OrderDto> findByUser(UserDto userDto, Pageable pageable);
    
    OrderDto createOrder(UserDto userDto, List<OrderItemDto> items, String shippingAddress, 
                        String paymentMethod, String paymentId);
    
    OrderDto updateOrderStatus(Long orderId, String status);
    
    void cancelOrder(Long orderId);
    
    Page<OrderDto> searchOrders(String userName, String bookTitle, String publisher, 
                               String author, String bookStatus, 
                               LocalDateTime startDate, LocalDateTime endDate, 
                               Pageable pageable);
    
    List<OrderDto> findRecentOrders(int limit);
    
    BigDecimal getTotalSalesAmount(LocalDateTime start, LocalDateTime end);
    
    long getOrderCountByStatus(String status);
    
    List<OrderDto> findOrdersRequiringAction();
}
