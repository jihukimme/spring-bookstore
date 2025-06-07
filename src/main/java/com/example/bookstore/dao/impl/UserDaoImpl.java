package com.example.bookstore.dao.impl;

import com.example.bookstore.dao.UserDao;
import com.example.bookstore.entity.User;
import com.example.bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {
    
    private final UserRepository userRepository;
    
    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    @Override
    public Optional<User> findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Override
    public User save(User user) {
        return userRepository.save(user);
    }
    
    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
    
    @Override
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }
    
    @Override
    public boolean existsByUserId(String userId) {
        return userRepository.existsByUserId(userId);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    public Page<User> findByUserIdContaining(String userId, Pageable pageable) {
        return userRepository.findByUserIdContaining(userId, pageable);
    }
    
    @Override
    public Page<User> findByNameContaining(String name, Pageable pageable) {
        return userRepository.findByNameContaining(name, pageable);
    }
    
    @Override
    public Page<User> findByEmailContaining(String email, Pageable pageable) {
        return userRepository.findByEmailContaining(email, pageable);
    }
    
    @Override
    public Page<User> findByStatus(User.UserStatus status, Pageable pageable) {
        return userRepository.findByStatus(status, pageable);
    }
    
    @Override
    public Page<User> findByGrade(User.UserGrade grade, Pageable pageable) {
        return userRepository.findByGrade(grade, pageable);
    }
    
    @Override
    public Page<User> findByJoinDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return userRepository.findByJoinDateBetween(start, end, pageable);
    }
    
    @Override
    public List<User> findInactiveUsers(LocalDateTime since) {
        return userRepository.findByLastLoginDateBefore(since);
    }
    
    @Override
    public long countByStatus(User.UserStatus status) {
        return userRepository.countByStatus(status);
    }
    
    @Override
    public long countByGrade(User.UserGrade grade) {
        return userRepository.countByGrade(grade);
    }
}
