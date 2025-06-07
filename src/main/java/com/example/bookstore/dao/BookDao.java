package com.example.bookstore.dao;

import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookDao {
    
    Page<Book> findAll(Pageable pageable);
    
    Optional<Book> findById(Long id);
    
    Book save(Book book);
    
    void deleteById(Long id);
    
    boolean existsById(Long id);
    
    Page<Book> findByTitleContaining(String title, Pageable pageable);
    
    Page<Book> findByAuthorContaining(String author, Pageable pageable);
    
    Page<Book> findByPublisherContaining(String publisher, Pageable pageable);
    
    Page<Book> findByCategory(Category category, Pageable pageable);
    
    Page<Book> findByStatus(Book.BookStatus status, Pageable pageable);
    
    Page<Book> findByStockQuantityLessThanEqual(Integer stockQuantity, Pageable pageable);
    
    Page<Book> findByRegisteredAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    List<Book> findBestSellers(Pageable pageable);
    
    List<Book> findNewReleases(LocalDateTime since, Pageable pageable);
    
    Page<Book> searchBooks(String keyword, Pageable pageable);
    
    List<Book> findLowStockBooks(int threshold);
    
    long countByStatus(Book.BookStatus status);
}
