package com.example.bookstore.dao.impl;

import com.example.bookstore.dao.CartItemDao;
import com.example.bookstore.entity.CartItem;
import com.example.bookstore.entity.User;
import com.example.bookstore.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CartItemDaoImpl implements CartItemDao {
    
    private final CartItemRepository cartItemRepository;
    
    @Override
    public List<CartItem> findByUser(User user) {
        return cartItemRepository.findByUser(user);
    }
    
    @Override
    public Optional<CartItem> findById(Long id) {
        return cartItemRepository.findById(id);
    }
    
    @Override
    public Optional<CartItem> findByUserAndBookId(User user, Long bookId) {
        return cartItemRepository.findByUserAndBookId(user, bookId);
    }
    
    @Override
    public CartItem save(CartItem cartItem) {
        return cartItemRepository.save(cartItem);
    }
    
    @Override
    public void deleteById(Long id) {
        cartItemRepository.deleteById(id);
    }
    
    @Override
    public void delete(CartItem cartItem) {
        cartItemRepository.delete(cartItem);
    }
    
    @Override
    public void deleteByUser(User user) {
        cartItemRepository.deleteByUser(user);
    }
    
    @Override
    public int countByUser(User user) {
        return findByUser(user).stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
    
    @Override
    public List<CartItem> findAbandonedCartItems(int daysOld) {
        // 실제 구현에서는 CartItem에 생성일시 필드가 필요
        // 여기서는 예시로 빈 리스트 반환
        return List.of();
    }
}
