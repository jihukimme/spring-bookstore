package com.example.bookstore.service;

import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Order;
import com.example.bookstore.entity.OrderItem;
import com.example.bookstore.entity.User;
import com.example.bookstore.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final BookService bookService;
    
    public Page<Order> findAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }
    
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }
    
    public Page<Order> findByUser(User user, Pageable pageable) {
        return orderRepository.findByUser(user, pageable);
    }
    
    @Transactional
    public Order createOrder(User user, List<OrderItem> items, String shippingAddress, String paymentMethod, String paymentId) {
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);
        order.setPaymentId(paymentId);
        order.setStatus(Order.OrderStatus.PAID);
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItem item : items) {
            item.setOrder(order);
            totalAmount = totalAmount.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            
            // Update book stock
            bookService.updateBookStock(item.getBook().getId(), item.getQuantity());
        }
        
        order.setItems(items);
        order.setTotalAmount(totalAmount);
        
        return orderRepository.save(order);
    }
    
    @Transactional
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setStatus(status);
        return orderRepository.save(order);
    }
    
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (order.getStatus() == Order.OrderStatus.SHIPPING || order.getStatus() == Order.OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot cancel order that is already shipped or delivered");
        }
        
        order.setStatus(Order.OrderStatus.CANCELED);
        orderRepository.save(order);
        
        // Return items to inventory
        for (OrderItem item : order.getItems()) {
            Book book = item.getBook();
            book.setStockQuantity(book.getStockQuantity() + item.getQuantity());
            if (book.getStatus() == Book.BookStatus.OUT_OF_STOCK) {
                book.setStatus(Book.BookStatus.SELLING);
            }
            bookService.saveBook(book);
        }
    }
    
    public Page<Order> searchOrders(String userName, String bookTitle, String publisher, 
                                   String author, Book.BookStatus bookStatus, 
                                   LocalDateTime startDate, LocalDateTime endDate, 
                                   Pageable pageable) {
        if (userName != null && !userName.isEmpty()) {
            return orderRepository.findByUserNameContaining(userName, pageable);
        } else if (bookTitle != null && !bookTitle.isEmpty()) {
            return orderRepository.findByBookTitleContaining(bookTitle, pageable);
        } else if (publisher != null && !publisher.isEmpty()) {
            return orderRepository.findByBookPublisherContaining(publisher, pageable);
        } else if (author != null && !author.isEmpty()) {
            return orderRepository.findByBookAuthorContaining(author, pageable);
        } else if (startDate != null && endDate != null) {
            return orderRepository.findByOrderDateBetween(startDate, endDate, pageable);
        } else {
            return orderRepository.findAll(pageable);
        }
    }
}
