package com.example.bookstore.service;

import com.example.bookstore.dto.CartItemDto;
import com.example.bookstore.dto.UserDto;

import java.util.List;

public interface CartService {
    
    List<CartItemDto> getCartItems(UserDto userDto);
    
    int getCartItemCount(UserDto userDto);
    
    CartItemDto addToCart(UserDto userDto, Long bookId, Integer quantity);
    
    void updateCartItemQuantity(Long cartItemId, Integer quantity);
    
    void removeFromCart(Long cartItemId);
    
    void clearCart(UserDto userDto);
    
    boolean validateCartItems(UserDto userDto);
    
    void cleanupAbandonedCarts(int daysOld);
}
