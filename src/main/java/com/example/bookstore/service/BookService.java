package com.example.bookstore.service;

import com.example.bookstore.dto.BookDto;
import com.example.bookstore.dto.CategoryDto;
import com.example.bookstore.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookService {
    
    Page<BookDto> findAllBooks(Pageable pageable);
    
    BookDto findById(Long id);
    
    Optional<BookDto> findByIdOptional(Long id);
    
    BookDto saveBook(BookDto bookDto);
    
    void deleteBook(Long id);
    
    Page<BookDto> searchBooks(String title, String publisher, String author, 
                             Integer stockQuantity, String status, 
                             LocalDateTime startDate, LocalDateTime endDate, 
                             Pageable pageable);
    
    Page<BookDto> findByCategory(CategoryDto categoryDto, Pageable pageable);
    
    List<BookDto> findBestSellers();
    
    List<BookDto> findNewReleases();
    
    void updateBookStock(Long bookId, int quantity);
    
    void restoreBookStock(Long bookId, int quantity);
    
    boolean isBookAvailable(Long bookId, int quantity);
    
    Page<BookDto> searchByKeyword(String keyword, Pageable pageable);
    
    List<BookDto> findLowStockBooks(int threshold);
    
    long getBookCountByStatus(String status);
}
