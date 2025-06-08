package com.example.bookstore.service.impl;

import com.example.bookstore.constants.BookstoreConstants;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.dto.BookDto;
import com.example.bookstore.dto.CategoryDto;
import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Category;
import com.example.bookstore.enums.BookStatus;
import com.example.bookstore.enums.CategoryLevel;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.exception.InsufficientStockException;
import com.example.bookstore.service.BookService;
import com.example.bookstore.util.PaginationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    
    private final BookRepository bookRepository;
    
    @Override
    public Page<BookDto> findAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(BookDto::fromEntity);
    }
    
    @Override
    public BookDto findById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book", id));
        return BookDto.fromEntity(book);
    }
    
    @Override
    public Optional<BookDto> findByIdOptional(Long id) {
        return bookRepository.findById(id).map(BookDto::fromEntity);
    }
    
    @Override
    @Transactional
    public BookDto saveBook(BookDto bookDto) {
        Book book = bookDto.toEntity();
        Book savedBook = bookRepository.save(book);
        log.info("Book saved: {}", savedBook.getTitle());
        return BookDto.fromEntity(savedBook);
    }
    
    @Override
    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException("Book", id);
        }
        bookRepository.deleteById(id);
        log.info("Book deleted with id: {}", id);
    }

//    @Override
//    public Page<BookDto> searchBooks(String title, String publisher, String author,
//                                    Integer stockQuantity, String statusStr,
//                                    LocalDateTime startDate, LocalDateTime endDate,
//                                    Pageable pageable) {
//        Page<Book> books;
//
//        Book.BookStatus status = null;
//        if (statusStr != null && !statusStr.isEmpty()) {
//            try {
//                status = Book.BookStatus.valueOf(statusStr);
//            } catch (IllegalArgumentException e) {
//                log.warn("Invalid book status: {}", statusStr);
//            }
//        }
//
//        if (title != null && !title.trim().isEmpty()) {
//            books = bookRepository.findByTitleContaining(title.trim(), pageable);
//        } else if (publisher != null && !publisher.trim().isEmpty()) {
//            books = bookRepository.findByPublisherContaining(publisher.trim(), pageable);
//        } else if (author != null && !author.trim().isEmpty()) {
//            books = bookRepository.findByAuthorContaining(author.trim(), pageable);
//        } else if (stockQuantity != null) {
//            books = bookRepository.findByStockQuantityLessThanEqual(stockQuantity, pageable);
//        } else if (status != null) {
//            books = bookRepository.findByStatus(status, pageable);
//        } else if (startDate != null && endDate != null) {
//            books = bookRepository.findByRegisteredAtBetween(startDate, endDate, pageable);
//        } else {
//            books = bookRepository.findAll(pageable);
//        }
//
//        return books.map(BookDto::fromEntity);
//    }
    
    @Override
    public Page<BookDto> findByCategory(CategoryDto categoryDto, Pageable pageable) {
        Category category = new Category();
        category.setId(categoryDto.getId());
        category.setName(categoryDto.getName());
        category.setLevel(CategoryLevel.valueOf(categoryDto.getLevel()));

        return bookRepository.findByCategory(category, pageable)
                .map(BookDto::fromEntity);
    }
    
    @Override
    public List<BookDto> findBestSellers() {
        Pageable pageable = PaginationUtils.createPageable(0, 
                BookstoreConstants.BESTSELLER_LIMIT, "salesQuantity", "desc");
        return bookRepository.findBestSellers(pageable).stream()
                .map(BookDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<BookDto> findNewReleases() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        Pageable pageable = PaginationUtils.createPageable(0, 
                BookstoreConstants.NEW_RELEASE_LIMIT, "registeredAt", "desc");
        return bookRepository.findNewReleases(oneMonthAgo, pageable).stream()
                .map(BookDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void updateBookStock(Long bookId, int quantity) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book", bookId));
        
        int newStock = book.getStockQuantity() - quantity;
        if (newStock < 0) {
            throw new InsufficientStockException(book.getTitle(), quantity, book.getStockQuantity());
        }
        
        book.setStockQuantity(newStock);
        
        if (newStock == 0) {
            book.setStatus(BookStatus.OUT_OF_STOCK);
        }
        
        bookRepository.save(book);
        log.info("Book stock updated: {} - New stock: {}", book.getTitle(), newStock);
    }
    
    @Override
    @Transactional
    public void restoreBookStock(Long bookId, int quantity) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book", bookId));
        
        int newStock = book.getStockQuantity() + quantity;
        book.setStockQuantity(newStock);
        
        if (book.getStatus() == BookStatus.OUT_OF_STOCK && newStock > 0) {
            book.setStatus(BookStatus.SELLING);
        }
        
        bookRepository.save(book);
        log.info("Book stock restored: {} - New stock: {}", book.getTitle(), newStock);
    }
    
    @Override
    public boolean isBookAvailable(Long bookId, int quantity) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book", bookId));
        
        return book.getStatus() == BookStatus.SELLING &&
               book.getStockQuantity() >= quantity;
    }
    
//    @Override
//    public Page<BookDto> searchByKeyword(String keyword, Pageable pageable) {
//        return bookRepository.searchBooks(keyword, pageable)
//                .map(BookDto::fromEntity);
//    }
//
//    @Override
//    public List<BookDto> findLowStockBooks(int threshold) {
//        return bookRepository.findLowStockBooks(threshold).stream()
//                .map(BookDto::fromEntity)
//                .collect(Collectors.toList());
//    }
    
    @Override
    public long getBookCountByStatus(String statusStr) {
        try {
            BookStatus status = BookStatus.valueOf(statusStr);
            return bookRepository.countByStatus(status);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid book status: {}", statusStr);
            return 0;
        }
    }
}
