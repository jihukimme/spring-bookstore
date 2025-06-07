package com.example.bookstore.dao;

import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Review;
import com.example.bookstore.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ReviewDao {
    
    List<Review> findByBook(Book book);
    
    List<Review> findByUser(User user);
    
    Page<Review> findByBook(Book book, Pageable pageable);
    
    Optional<Review> findById(Long id);
    
    Review save(Review review);
    
    void delete(Review review);
    
    void deleteById(Long id);
    
    Double getAverageRatingForBook(Book book);
    
    long countByBook(Book book);
    
    boolean existsByUserAndBook(User user, Book book);
}
