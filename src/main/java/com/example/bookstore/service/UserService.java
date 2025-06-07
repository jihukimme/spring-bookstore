package com.example.bookstore.service;

import com.example.bookstore.entity.User;
import com.example.bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public Page<User> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }
    
    public boolean existsByUserId(String userId) {
        return userRepository.existsByUserId(userId);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Transactional
    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.getRoles().add(User.UserRole.ROLE_USER);
        return userRepository.save(user);
    }
    
    @Transactional
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    @Transactional
    public void updateLastLoginDate(String userId) {
        userRepository.findByUserId(userId).ifPresent(user -> {
            user.setLastLoginDate(LocalDateTime.now());
            userRepository.save(user);
        });
    }
    
    public Page<User> searchUsers(String userId, User.UserStatus status, String email, 
                                 LocalDateTime startDate, LocalDateTime endDate, 
                                 User.UserGrade grade, String name, Pageable pageable) {
        if (userId != null && !userId.isEmpty()) {
            return userRepository.findByUserIdContaining(userId, pageable);
        } else if (status != null) {
            return userRepository.findByStatus(status, pageable);
        } else if (email != null && !email.isEmpty()) {
            return userRepository.findByEmailContaining(email, pageable);
        } else if (startDate != null && endDate != null) {
            return userRepository.findByJoinDateBetween(startDate, endDate, pageable);
        } else if (grade != null) {
            return userRepository.findByGrade(grade, pageable);
        } else if (name != null && !name.isEmpty()) {
            return userRepository.findByNameContaining(name, pageable);
        } else {
            return userRepository.findAll(pageable);
        }
    }
}
