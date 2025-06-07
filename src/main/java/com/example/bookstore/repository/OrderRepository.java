package com.example.bookstore.repository;

import com.example.bookstore.entity.Order;
import com.example.bookstore.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Page<Order> findByUser(User user, Pageable pageable);
    
    Page<Order> findByOrderDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);
    
    @Query("SELECT o FROM Order o JOIN o.user u WHERE u.name LIKE %:name%")
    Page<Order> findByUserNameContaining(@Param("name") String name, Pageable pageable);
    
    @Query("SELECT o FROM Order o JOIN o.items i JOIN i.book b WHERE b.title LIKE %:title%")
    Page<Order> findByBookTitleContaining(@Param("title") String title, Pageable pageable);
    
    @Query("SELECT o FROM Order o JOIN o.items i JOIN i.book b WHERE b.publisher LIKE %:publisher%")
    Page<Order> findByBookPublisherContaining(@Param("publisher") String publisher, Pageable pageable);
    
    @Query("SELECT o FROM Order o JOIN o.items i JOIN i.book b WHERE b.author LIKE %:author%")
    Page<Order> findByBookAuthorContaining(@Param("author") String author, Pageable pageable);
}
