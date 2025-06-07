package com.example.bookstore.repository;

import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Review;
import com.example.bookstore.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    List<Review> findByBook(Book book);
    
    List<Review> findByUser(User user);
    
    Page<Review> findByBook(Book book, Pageable pageable);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book = :book")
    Double getAverageRatingForBook(@Param("book") Book book);
}
