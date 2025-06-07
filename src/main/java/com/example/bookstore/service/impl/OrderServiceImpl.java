package com.example.bookstore.service.impl;

import com.example.bookstore.dao.BookDao;
import com.example.bookstore.dao.OrderDao;
import com.example.bookstore.dao.UserDao;
import com.example.bookstore.dto.OrderDto;
import com.example.bookstore.dto.OrderItemDto;
import com.example.bookstore.dto.UserDto;
import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Order;
import com.example.bookstore.entity.OrderItem;
import com.example.bookstore.entity.User;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.service.BookService;
import com.example.bookstore.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    
    private final OrderDao orderDao;
    private final UserDao userDao;
    private final BookDao bookDao;
    private final BookService bookService;
    
    @Override
    public Page<OrderDto> findAllOrders(Pageable pageable) {
        return orderDao.findAll(pageable)
                .map(OrderDto::fromEntity);
    }
    
    @Override
    public OrderDto findById(Long id) {
        Order order = orderDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order", id));
        return OrderDto.fromEntity(order);
    }
    
    @Override
    public Page<OrderDto> findByUser(UserDto userDto, Pageable pageable) {
        User user = userDao.findById(userDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("User", userDto.getId()));
        
        return orderDao.findByUser(user, pageable)
                .map(OrderDto::fromEntity);
    }
    
    @Override
    @Transactional
    public OrderDto createOrder(UserDto userDto, List<OrderItemDto> itemDtos, String shippingAddress, 
                               String paymentMethod, String paymentId) {
        User user = userDao.findById(userDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("User", userDto.getId()));
        
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);
        order.setPaymentId(paymentId);
        order.setStatus(Order.OrderStatus.PAID);
        
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (OrderItemDto itemDto : itemDtos) {
            Book book = bookDao.findById(itemDto.getBookId())
                    .orElseThrow(() -> new EntityNotFoundException("Book", itemDto.getBookId()));
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(book);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setPrice(itemDto.getPrice());
            orderItem.setBookStatus(book.getStatus());
            
            orderItems.add(orderItem);
            totalAmount = totalAmount.add(itemDto.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
            
            // Update book stock
            bookService.updateBookStock(book.getId(), itemDto.getQuantity());
        }
        
        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);
        
        Order savedOrder = orderDao.save(order);
        log.info("Order created: {} for user: {}", savedOrder.getId(), user.getUserId());
        
        return OrderDto.fromEntity(savedOrder);
    }
    
    @Override
    @Transactional
    public OrderDto updateOrderStatus(Long orderId, String statusStr) {
        Order order = orderDao.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order", orderId));
        
        try {
            Order.OrderStatus status = Order.OrderStatus.valueOf(statusStr);
            order.setStatus(status);
            Order savedOrder = orderDao.save(order);
            log.info("Order status updated: {} - New status: {}", orderId, status);
            
            return OrderDto.fromEntity(savedOrder);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid order status: {}", statusStr);
            throw new IllegalArgumentException("Invalid order status: " + statusStr);
        }
    }
    
    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderDao.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order", orderId));
        
        if (order.getStatus() == Order.OrderStatus.SHIPPING || 
            order.getStatus() == Order.OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot cancel order that is already shipped or delivered");
        }
        
        order.setStatus(Order.OrderStatus.CANCELED);
        orderDao.save(order);
        
        // Return items to inventory
        for (OrderItem item : order.getItems()) {
            bookService.restoreBookStock(item.getBook().getId(), item.getQuantity());
        }
        
        log.info("Order canceled: {}", orderId);
    }
    
    @Override
    public Page<OrderDto> searchOrders(String userName, String bookTitle, String publisher, 
                                      String author, String bookStatusStr, 
                                      LocalDateTime startDate, LocalDateTime endDate, 
                                      Pageable pageable) {
        Page<Order> orders;
        
        Book.BookStatus bookStatus = null;
        if (bookStatusStr != null && !bookStatusStr.isEmpty()) {
            try {
                bookStatus = Book.BookStatus.valueOf(bookStatusStr);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid book status: {}", bookStatusStr);
            }
        }
        
        if (userName != null && !userName.trim().isEmpty()) {
            orders = orderDao.findByUserNameContaining(userName.trim(), pageable);
        } else if (bookTitle != null && !bookTitle.trim().isEmpty()) {
            orders = orderDao.findByBookTitleContaining(bookTitle.trim(), pageable);
        } else if (publisher != null && !publisher.trim().isEmpty()) {
            orders = orderDao.findByBookPublisherContaining(publisher.trim(), pageable);
        } else if (author != null && !author.trim().isEmpty()) {
            orders = orderDao.findByBookAuthorContaining(author.trim(), pageable);
        } else if (startDate != null && endDate != null) {
            orders = orderDao.findByOrderDateBetween(startDate, endDate, pageable);
        } else {
            orders = orderDao.findAll(pageable);
        }
        
        return orders.map(OrderDto::fromEntity);
    }
    
    @Override
    public List<OrderDto> findRecentOrders(int limit) {
        return orderDao.findRecentOrders(limit).stream()
                .map(OrderDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public BigDecimal getTotalSalesAmount(LocalDateTime start, LocalDateTime end) {
        return orderDao.getTotalSalesAmount(start, end);
    }
    
    @Override
    public long getOrderCountByStatus(String statusStr) {
        try {
            Order.OrderStatus status = Order.OrderStatus.valueOf(statusStr);
            return orderDao.countByStatus(status);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid order status: {}", statusStr);
            return 0;
        }
    }
    
    @Override
    public List<OrderDto> findOrdersRequiringAction() {
        return orderDao.findOrdersRequiringAction().stream()
                .map(OrderDto::fromEntity)
                .collect(Collectors.toList());
    }
}
