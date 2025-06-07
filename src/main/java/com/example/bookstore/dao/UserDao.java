package com.example.bookstore.dao;

import com.example.bookstore.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserDao {
    
    Page<User> findAll(Pageable pageable);
    
    Optional<User> findById(Long id);
    
    Optional<User> findByUserId(String userId);
    
    Optional<User> findByEmail(String email);
    
    User save(User user);
    
    void deleteById(Long id);
    
    boolean existsById(Long id);
    
    boolean existsByUserId(String userId);
    
    boolean existsByEmail(String email);
    
    Page<User> findByUserIdContaining(String userId, Pageable pageable);
    
    Page<User> findByNameContaining(String name, Pageable pageable);
    
    Page<User> findByEmailContaining(String email, Pageable pageable);
    
    Page<User> findByStatus(User.UserStatus status, Pageable pageable);
    
    Page<User> findByGrade(User.UserGrade grade, Pageable pageable);
    
    Page<User> findByJoinDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    List<User> findInactiveUsers(LocalDateTime since);
    
    long countByStatus(User.UserStatus status);
    
    long countByGrade(User.UserGrade grade);
}
