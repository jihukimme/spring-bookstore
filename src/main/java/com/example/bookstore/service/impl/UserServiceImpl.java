package com.example.bookstore.service.impl;

import com.example.bookstore.dao.UserDao;
import com.example.bookstore.dto.UserDto;
import com.example.bookstore.entity.User;
import com.example.bookstore.exception.DuplicateEntityException;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserDao userDao;
    
    @Override
    public Page<UserDto> findAllUsers(Pageable pageable) {
        return userDao.findAll(pageable)
                .map(UserDto::fromEntity);
    }
    
    @Override
    public UserDto findById(Long id) {
        User user = userDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id));
        return UserDto.fromEntity(user);
    }
    
    @Override
    public Optional<UserDto> findByIdOptional(Long id) {
        return userDao.findById(id).map(UserDto::fromEntity);
    }
    
    @Override
    public Optional<UserDto> findByUserId(String userId) {
        return userDao.findByUserId(userId).map(UserDto::fromEntity);
    }
    
    @Override
    public boolean existsByUserId(String userId) {
        return userDao.existsByUserId(userId);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return userDao.existsByEmail(email);
    }
    
    @Override
    @Transactional
    public UserDto registerUser(UserDto userDto) {
        // Check for duplicates
        if (userDao.existsByUserId(userDto.getUserId())) {
            throw new DuplicateEntityException("User", "userId", userDto.getUserId());
        }
        
        if (userDao.existsByEmail(userDto.getEmail())) {
            throw new DuplicateEntityException("User", "email", userDto.getEmail());
        }
        
        User user = userDto.toEntity();
        
        // Set default values
        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }
        user.getRoles().add(User.UserRole.ROLE_USER);
        
        if (user.getStatus() == null) {
            user.setStatus(User.UserStatus.ACTIVE);
        }
        
        if (user.getGrade() == null) {
            user.setGrade(User.UserGrade.REGULAR);
        }
        
        User savedUser = userDao.save(user);
        log.info("User registered: {}", savedUser.getUserId());
        
        return UserDto.fromEntity(savedUser);
    }
    
    @Override
    @Transactional
    public UserDto updateUser(UserDto userDto) {
        User existingUser = userDao.findById(userDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("User", userDto.getId()));
        
        // Update fields
        existingUser.setName(userDto.getName());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setPhone(userDto.getPhone());
        existingUser.setAddress(userDto.getAddress());
        
        // Convert enum strings to actual enum values
        if (userDto.getStatus() != null) {
            try {
                existingUser.setStatus(User.UserStatus.valueOf(userDto.getStatus()));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid user status: {}", userDto.getStatus());
            }
        }
        
        if (userDto.getGrade() != null) {
            try {
                existingUser.setGrade(User.UserGrade.valueOf(userDto.getGrade()));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid user grade: {}", userDto.getGrade());
            }
        }
        
        User savedUser = userDao.save(existingUser);
        log.info("User updated: {}", savedUser.getUserId());
        
        return UserDto.fromEntity(savedUser);
    }
    
    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userDao.existsById(id)) {
            throw new EntityNotFoundException("User", id);
        }
        userDao.deleteById(id);
        log.info("User deleted with id: {}", id);
    }
    
    @Override
    @Transactional
    public void updateLastLoginDate(String userId) {
        userDao.findByUserId(userId).ifPresent(user -> {
            user.setLastLoginDate(LocalDateTime.now());
            userDao.save(user);
            log.debug("Last login date updated for user: {}", userId);
        });
    }
    
    @Override
    public Page<UserDto> searchUsers(String userId, String statusStr, String email, 
                                    LocalDateTime startDate, LocalDateTime endDate, 
                                    String gradeStr, String name, Pageable pageable) {
        Page<User> users;
        
        User.UserStatus status = null;
        if (statusStr != null && !statusStr.isEmpty()) {
            try {
                status = User.UserStatus.valueOf(statusStr);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid user status: {}", statusStr);
            }
        }
        
        User.UserGrade grade = null;
        if (gradeStr != null && !gradeStr.isEmpty()) {
            try {
                grade = User.UserGrade.valueOf(gradeStr);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid user grade: {}", gradeStr);
            }
        }
        
        if (userId != null && !userId.trim().isEmpty()) {
            users = userDao.findByUserIdContaining(userId.trim(), pageable);
        } else if (status != null) {
            users = userDao.findByStatus(status, pageable);
        } else if (email != null && !email.trim().isEmpty()) {
            users = userDao.findByEmailContaining(email.trim(), pageable);
        } else if (startDate != null && endDate != null) {
            users = userDao.findByJoinDateBetween(startDate, endDate, pageable);
        } else if (grade != null) {
            users = userDao.findByGrade(grade, pageable);
        } else if (name != null && !name.trim().isEmpty()) {
            users = userDao.findByNameContaining(name.trim(), pageable);
        } else {
            users = userDao.findAll(pageable);
        }
        
        return users.map(UserDto::fromEntity);
    }
    
    @Override
    public UserDto authenticate(String userId, String password) {
        User user = userDao.findByUserId(userId).orElse(null);
        
        if (user != null && user.getPassword().equals(password)) {
            updateLastLoginDate(userId);
            log.info("User authenticated: {}", userId);
            return UserDto.fromEntity(user);
        }
        
        log.warn("Authentication failed for user: {}", userId);
        return null;
    }
    
    @Override
    public List<UserDto> findInactiveUsers(int daysInactive) {
        LocalDateTime threshold = LocalDateTime.now().minusDays(daysInactive);
        return userDao.findInactiveUsers(threshold).stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public long getUserCountByStatus(String statusStr) {
        try {
            User.UserStatus status = User.UserStatus.valueOf(statusStr);
            return userDao.countByStatus(status);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid user status: {}", statusStr);
            return 0;
        }
    }
    
    @Override
    public long getUserCountByGrade(String gradeStr) {
        try {
            User.UserGrade grade = User.UserGrade.valueOf(gradeStr);
            return userDao.countByGrade(grade);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid user grade: {}", gradeStr);
            return 0;
        }
    }
}
