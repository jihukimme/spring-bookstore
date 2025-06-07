package com.example.bookstore.service.impl;

import com.example.bookstore.dao.BookDao;
import com.example.bookstore.dao.CartItemDao;
import com.example.bookstore.dao.UserDao;
import com.example.bookstore.dto.CartItemDto;
import com.example.bookstore.dto.UserDto;
import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.CartItem;
import com.example.bookstore.entity.User;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.exception.InsufficientStockException;
import com.example.bookstore.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    
    private final CartItemDao cartItemDao;
    private final BookDao bookDao;
    private final UserDao userDao;
    
    @Override
    public List<CartItemDto> getCartItems(UserDto userDto) {
        User user = userDao.findById(userDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("User", userDto.getId()));
        
        List<CartItem> cartItems = cartItemDao.findByUser(user);
        return cartItems.stream()
                .map(CartItemDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public int getCartItemCount(UserDto userDto) {
        User user = userDao.findById(userDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("User", userDto.getId()));
        
        return cartItemDao.countByUser(user);
    }
    
    @Override
    @Transactional
    public CartItemDto addToCart(UserDto userDto, Long bookId, Integer quantity) {
        User user = userDao.findById(userDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("User", userDto.getId()));
        
        Book book = bookDao.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book", bookId));
        
        // Check stock availability
        if (book.getStockQuantity() < quantity) {
            throw new InsufficientStockException(book.getTitle(), quantity, book.getStockQuantity());
        }
        
        CartItem cartItem = cartItemDao.findByUserAndBookId(user, bookId)
                .map(existingItem -> {
                    int newQuantity = existingItem.getQuantity() + quantity;
                    if (book.getStockQuantity() < newQuantity) {
                        throw new InsufficientStockException(book.getTitle(), newQuantity, book.getStockQuantity());
                    }
                    existingItem.setQuantity(newQuantity);
                    return existingItem;
                })
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setUser(user);
                    newItem.setBook(book);
                    newItem.setQuantity(quantity);
                    return newItem;
                });
        
        CartItem savedItem = cartItemDao.save(cartItem);
        log.info("Item added to cart: {} - Quantity: {}", book.getTitle(), quantity);
        
        return CartItemDto.fromEntity(savedItem);
    }
    
    @Override
    @Transactional
    public void updateCartItemQuantity(Long cartItemId, Integer quantity) {
        CartItem item = cartItemDao.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("CartItem", cartItemId));
        
        if (quantity <= 0) {
            cartItemDao.delete(item);
            log.info("Cart item removed: {}", item.getBook().getTitle());
        } else {
            // Check stock availability
            if (item.getBook().getStockQuantity() < quantity) {
                throw new InsufficientStockException(item.getBook().getTitle(), 
                        quantity, item.getBook().getStockQuantity());
            }
            
            item.setQuantity(quantity);
            cartItemDao.save(item);
            log.info("Cart item quantity updated: {} - New quantity: {}", 
                    item.getBook().getTitle(), quantity);
        }
    }
    
    @Override
    @Transactional
    public void removeFromCart(Long cartItemId) {
        CartItem item = cartItemDao.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("CartItem", cartItemId));
        
        cartItemDao.delete(item);
        log.info("Cart item removed: {}", item.getBook().getTitle());
    }
    
    @Override
    @Transactional
    public void clearCart(UserDto userDto) {
        User user = userDao.findById(userDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("User", userDto.getId()));
        
        cartItemDao.deleteByUser(user);
        log.info("Cart cleared for user: {}", user.getUserId());
    }
    
    @Override
    public boolean validateCartItems(UserDto userDto) {
        User user = userDao.findById(userDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("User", userDto.getId()));
        
        List<CartItem> cartItems = cartItemDao.findByUser(user);
        
        for (CartItem item : cartItems) {
            if (item.getBook().getStockQuantity() < item.getQuantity()) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    @Transactional
    public void cleanupAbandonedCarts(int daysOld) {
        List<CartItem> abandonedItems = cartItemDao.findAbandonedCartItems(daysOld);
        for (CartItem item : abandonedItems) {
            cartItemDao.delete(item);
        }
        log.info("Cleaned up {} abandoned cart items", abandonedItems.size());
    }
}
