package com.example.bookstore.dao;

import com.example.bookstore.entity.Order;
import com.example.bookstore.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderDao {
    
    Page<Order> findAll(Pageable pageable);
    
    Optional<Order> findById(Long id);
    
    Order save(Order order);
    
    void deleteById(Long id);
    
    Page<Order> findByUser(User user, Pageable pageable);
    
    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);
    
    Page<Order> findByOrderDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    Page<Order> findByUserNameContaining(String name, Pageable pageable);
    
    Page<Order> findByBookTitleContaining(String title, Pageable pageable);
    
    Page<Order> findByBookPublisherContaining(String publisher, Pageable pageable);
    
    Page<Order> findByBookAuthorContaining(String author, Pageable pageable);
    
    List<Order> findRecentOrders(int limit);
    
    BigDecimal getTotalSalesAmount(LocalDateTime start, LocalDateTime end);
    
    long countByStatus(Order.OrderStatus status);
    
    List<Order> findOrdersRequiringAction();
}
