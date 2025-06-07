package com.example.bookstore.service;

import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Category;
import com.example.bookstore.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {
    
    private final BookRepository bookRepository;
    
    public Page<Book> findAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }
    
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }
    
    @Transactional
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }
    
    @Transactional
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
    
    public Page<Book> searchBooks(String title, String publisher, String author, 
                                 Integer stockQuantity, Book.BookStatus status, 
                                 LocalDateTime startDate, LocalDateTime endDate, 
                                 Pageable pageable) {
        if (title != null && !title.isEmpty()) {
            return bookRepository.findByTitleContaining(title, pageable);
        } else if (publisher != null && !publisher.isEmpty()) {
            return bookRepository.findByPublisherContaining(publisher, pageable);
        } else if (author != null && !author.isEmpty()) {
            return bookRepository.findByAuthorContaining(author, pageable);
        } else if (stockQuantity != null) {
            return bookRepository.findByStockQuantityLessThanEqual(stockQuantity, pageable);
        } else if (status != null) {
            return bookRepository.findByStatus(status, pageable);
        } else if (startDate != null && endDate != null) {
            return bookRepository.findByRegisteredAtBetween(startDate, endDate, pageable);
        } else {
            return bookRepository.findAll(pageable);
        }
    }
    
    public Page<Book> findByCategory(Category category, Pageable pageable) {
        return bookRepository.findByCategory(category, pageable);
    }
    
    public List<Book> findBestSellers(int limit) {
        return bookRepository.findBestSellers(Pageable.ofSize(limit));
    }
    
    public List<Book> findNewReleases(int limit) {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        return bookRepository.findNewReleases(oneMonthAgo, Pageable.ofSize(limit));
    }
    
    @Transactional
    public void updateBookStock(Long bookId, int quantity) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        
        int newStock = book.getStockQuantity() - quantity;
        if (newStock < 0) {
            throw new RuntimeException("Not enough stock available");
        }
        
        book.setStockQuantity(newStock);
        
        if (newStock == 0) {
            book.setStatus(Book.BookStatus.OUT_OF_STOCK);
        }
        
        bookRepository.save(book);
    }
}
