package com.example.bookstore.dao.impl;

import com.example.bookstore.dao.OrderDao;
import com.example.bookstore.entity.Order;
import com.example.bookstore.entity.User;
import com.example.bookstore.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OrderDaoImpl implements OrderDao {
    
    private final OrderRepository orderRepository;
    
    @Override
    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }
    
    @Override
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }
    
    @Override
    public Order save(Order order) {
        return orderRepository.save(order);
    }
    
    @Override
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }
    
    @Override
    public Page<Order> findByUser(User user, Pageable pageable) {
        return orderRepository.findByUser(user, pageable);
    }
    
    @Override
    public Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable);
    }
    
    @Override
    public Page<Order> findByOrderDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return orderRepository.findByOrderDateBetween(start, end, pageable);
    }
    
    @Override
    public Page<Order> findByUserNameContaining(String name, Pageable pageable) {
        return orderRepository.findByUserNameContaining(name, pageable);
    }
    
    @Override
    public Page<Order> findByBookTitleContaining(String title, Pageable pageable) {
        return orderRepository.findByBookTitleContaining(title, pageable);
    }
    
    @Override
    public Page<Order> findByBookPublisherContaining(String publisher, Pageable pageable) {
        return orderRepository.findByBookPublisherContaining(publisher, pageable);
    }
    
    @Override
    public Page<Order> findByBookAuthorContaining(String author, Pageable pageable) {
        return orderRepository.findByBookAuthorContaining(author, pageable);
    }
    
    @Override
    public List<Order> findRecentOrders(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "orderDate"));
        return orderRepository.findAll(pageable).getContent();
    }
    
    @Override
    public BigDecimal getTotalSalesAmount(LocalDateTime start, LocalDateTime end) {
        return orderRepository.getTotalSalesAmount(start, end);
    }
    
    @Override
    public long countByStatus(Order.OrderStatus status) {
        return orderRepository.countByStatus(status);
    }
    
    @Override
    public List<Order> findOrdersRequiringAction() {
        // 처리가 필요한 주문들 (예: 결제 완료 후 24시간 이상 지난 주문)
        LocalDateTime threshold = LocalDateTime.now().minusHours(24);
        return orderRepository.findByStatusAndOrderDateBefore(Order.OrderStatus.PAID, threshold);
    }
}
