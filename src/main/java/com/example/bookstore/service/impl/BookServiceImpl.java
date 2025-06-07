package com.example.bookstore.service.impl;

import com.example.bookstore.constants.BookstoreConstants;
import com.example.bookstore.dao.BookDao;
import com.example.bookstore.dto.BookDto;
import com.example.bookstore.dto.CategoryDto;
import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Category;
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
    
    private final BookDao bookDao;
    
    @Override
    public Page<BookDto> findAllBooks(Pageable pageable) {
        return bookDao.findAll(pageable)
                .map(BookDto::fromEntity);
    }
    
    @Override
    public BookDto findById(Long id) {
        Book book = bookDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book", id));
        return BookDto.fromEntity(book);
    }
    
    @Override
    public Optional<BookDto> findByIdOptional(Long id) {
        return bookDao.findById(id).map(BookDto::fromEntity);
    }
    
    @Override
    @Transactional
    public BookDto saveBook(BookDto bookDto) {
        Book book = bookDto.toEntity();
        Book savedBook = bookDao.save(book);
        log.info("Book saved: {}", savedBook.getTitle());
        return BookDto.fromEntity(savedBook);
    }
    
    @Override
    @Transactional
    public void deleteBook(Long id) {
        if (!bookDao.existsById(id)) {
            throw new EntityNotFoundException("Book", id);
        }
        bookDao.deleteById(id);
        log.info("Book deleted with id: {}", id);
    }
    
    @Override
    public Page<BookDto> searchBooks(String title, String publisher, String author, 
                                    Integer stockQuantity, String statusStr, 
                                    LocalDateTime startDate, LocalDateTime endDate, 
                                    Pageable pageable) {
        Page<Book> books;
        
        Book.BookStatus status = null;
        if (statusStr != null && !statusStr.isEmpty()) {
            try {
                status = Book.BookStatus.valueOf(statusStr);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid book status: {}", statusStr);
            }
        }
        
        if (title != null && !title.trim().isEmpty()) {
            books = bookDao.findByTitleContaining(title.trim(), pageable);
        } else if (publisher != null && !publisher.trim().isEmpty()) {
            books = bookDao.findByPublisherContaining(publisher.trim(), pageable);
        } else if (author != null && !author.trim().isEmpty()) {
            books = bookDao.findByAuthorContaining(author.trim(), pageable);
        } else if (stockQuantity != null) {
            books = bookDao.findByStockQuantityLessThanEqual(stockQuantity, pageable);
        } else if (status != null) {
            books = bookDao.findByStatus(status, pageable);
        } else if (startDate != null && endDate != null) {
            books = bookDao.findByRegisteredAtBetween(startDate, endDate, pageable);
        } else {
            books = bookDao.findAll(pageable);
        }
        
        return books.map(BookDto::fromEntity);
    }
    
    @Override
    public Page<BookDto> findByCategory(CategoryDto categoryDto, Pageable pageable) {
        Category category = new Category();
        category.setId(categoryDto.getId());
        category.setName(categoryDto.getName());
        category.setLevel(Category.CategoryLevel.valueOf(categoryDto.getLevel()));
        
        return bookDao.findByCategory(category, pageable)
                .map(BookDto::fromEntity);
    }
    
    @Override
    public List<BookDto> findBestSellers() {
        Pageable pageable = PaginationUtils.createPageable(0, 
                BookstoreConstants.BESTSELLER_LIMIT, "salesIndex", "desc");
        return bookDao.findBestSellers(pageable).stream()
                .map(BookDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<BookDto> findNewReleases() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        Pageable pageable = PaginationUtils.createPageable(0, 
                BookstoreConstants.NEW_RELEASE_LIMIT, "registeredAt", "desc");
        return bookDao.findNewReleases(oneMonthAgo, pageable).stream()
                .map(BookDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void updateBookStock(Long bookId, int quantity) {
        Book book = bookDao.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book", bookId));
        
        int newStock = book.getStockQuantity() - quantity;
        if (newStock < 0) {
            throw new InsufficientStockException(book.getTitle(), quantity, book.getStockQuantity());
        }
        
        book.setStockQuantity(newStock);
        
        if (newStock == 0) {
            book.setStatus(Book.BookStatus.OUT_OF_STOCK);
        }
        
        bookDao.save(book);
        log.info("Book stock updated: {} - New stock: {}", book.getTitle(), newStock);
    }
    
    @Override
    @Transactional
    public void restoreBookStock(Long bookId, int quantity) {
        Book book = bookDao.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book", bookId));
        
        int newStock = book.getStockQuantity() + quantity;
        book.setStockQuantity(newStock);
        
        if (book.getStatus() == Book.BookStatus.OUT_OF_STOCK && newStock > 0) {
            book.setStatus(Book.BookStatus.SELLING);
        }
        
        bookDao.save(book);
        log.info("Book stock restored: {} - New stock: {}", book.getTitle(), newStock);
    }
    
    @Override
    public boolean isBookAvailable(Long bookId, int quantity) {
        Book book = bookDao.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book", bookId));
        
        return book.getStatus() == Book.BookStatus.SELLING && 
               book.getStockQuantity() >= quantity;
    }
    
    @Override
    public Page<BookDto> searchByKeyword(String keyword, Pageable pageable) {
        return bookDao.searchBooks(keyword, pageable)
                .map(BookDto::fromEntity);
    }
    
    @Override
    public List<BookDto> findLowStockBooks(int threshold) {
        return bookDao.findLowStockBooks(threshold).stream()
                .map(BookDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public long getBookCountByStatus(String statusStr) {
        try {
            Book.BookStatus status = Book.BookStatus.valueOf(statusStr);
            return bookDao.countByStatus(status);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid book status: {}", statusStr);
            return 0;
        }
    }
}
