package com.example.bookstore.repository;

import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Category;
import com.example.bookstore.enums.BookStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    
    Page<Book> findByTitleContaining(String title, Pageable pageable);
    
    Page<Book> findByPublisherContaining(String publisher, Pageable pageable);
    
    Page<Book> findByAuthorContaining(String author, Pageable pageable);
    
    Page<Book> findByStockQuantityLessThanEqual(Integer stockQuantity, Pageable pageable);
    
    Page<Book> findByStatus(BookStatus status, Pageable pageable);
    
    Page<Book> findByRegisteredAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    Page<Book> findByCategory(Category category, Pageable pageable);
    
    // 복합 검색을 위한 메서드 추가
    Page<Book> findByTitleContainingOrAuthorContainingOrPublisherContaining(
            String title, String author, String publisher, Pageable pageable);
    
    @Query("SELECT b FROM Book b ORDER BY b.salesQuantity DESC")
    List<Book> findBestSellers(Pageable pageable);
    
    @Query("SELECT b FROM Book b WHERE b.registeredAt >= :oneMonthAgo ORDER BY b.registeredAt DESC")
    List<Book> findNewReleases(@Param("oneMonthAgo") LocalDateTime oneMonthAgo, Pageable pageable);
    
    // 상태별 개수 조회
    long countByStatus(BookStatus status);
}
