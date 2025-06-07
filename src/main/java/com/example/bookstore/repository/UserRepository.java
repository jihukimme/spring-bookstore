package com.example.bookstore.repository;

import com.example.bookstore.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUserId(String userId);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUserId(String userId);
    
    boolean existsByEmail(String email);
    
    Page<User> findByUserIdContaining(String userId, Pageable pageable);
    
    Page<User> findByStatus(User.UserStatus status, Pageable pageable);
    
    Page<User> findByEmailContaining(String email, Pageable pageable);
    
    Page<User> findByJoinDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    Page<User> findByGrade(User.UserGrade grade, Pageable pageable);
    
    Page<User> findByNameContaining(String name, Pageable pageable);
    
    // 비활성 사용자 조회를 위한 메서드 추가
    List<User> findByLastLoginDateBefore(LocalDateTime date);
    
    // 상태별/등급별 개수 조회
    long countByStatus(User.UserStatus status);
    
    long countByGrade(User.UserGrade grade);
}
