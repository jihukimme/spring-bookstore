package com.example.bookstore.dao;

import com.example.bookstore.entity.CartItem;
import com.example.bookstore.entity.User;

import java.util.List;
import java.util.Optional;

public interface CartItemDao {
    
    List<CartItem> findByUser(User user);
    
    Optional<CartItem> findById(Long id);
    
    Optional<CartItem> findByUserAndBookId(User user, Long bookId);
    
    CartItem save(CartItem cartItem);
    
    void deleteById(Long id);
    
    void delete(CartItem cartItem);
    
    void deleteByUser(User user);
    
    int countByUser(User user);
    
    List<CartItem> findAbandonedCartItems(int daysOld);
}
