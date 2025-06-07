package com.example.bookstore.service;

import com.example.bookstore.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserService {
    
    Page<UserDto> findAllUsers(Pageable pageable);
    
    UserDto findById(Long id);
    
    Optional<UserDto> findByIdOptional(Long id);
    
    Optional<UserDto> findByUserId(String userId);
    
    boolean existsByUserId(String userId);
    
    boolean existsByEmail(String email);
    
    UserDto registerUser(UserDto userDto);
    
    UserDto updateUser(UserDto userDto);
    
    void deleteUser(Long id);
    
    void updateLastLoginDate(String userId);
    
    Page<UserDto> searchUsers(String userId, String status, String email, 
                             LocalDateTime startDate, LocalDateTime endDate, 
                             String grade, String name, Pageable pageable);
    
    UserDto authenticate(String userId, String password);
    
    List<UserDto> findInactiveUsers(int daysInactive);
    
    long getUserCountByStatus(String status);
    
    long getUserCountByGrade(String grade);
}
