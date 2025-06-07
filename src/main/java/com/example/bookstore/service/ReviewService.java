package com.example.bookstore.service;

import com.example.bookstore.dto.ReviewDto;
import com.example.bookstore.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {
    
    List<ReviewDto> findByBookId(Long bookId);
    
    List<ReviewDto> findByUser(UserDto userDto);
    
    Page<ReviewDto> findByBookId(Long bookId, Pageable pageable);
    
    ReviewDto addReview(UserDto userDto, Long bookId, Integer rating, String content);
    
    void deleteReview(Long reviewId);
    
    boolean canUserReview(UserDto userDto, Long bookId);
    
    Double getAverageRatingForBook(Long bookId);
    
    long getReviewCountForBook(Long bookId);
}
