//package com.example.bookstore.dao.impl;
//
//import com.example.bookstore.dao.BookDao;
//import com.example.bookstore.entity.Book;
//import com.example.bookstore.entity.Category;
//import com.example.bookstore.enums.BookStatus;
//import com.example.bookstore.repository.BookRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//@Slf4j
//@Repository
//@RequiredArgsConstructor
//public class BookDaoImpl implements BookDao {
//
//    private final BookRepository bookRepository;
//
//    @Override
//    public Page<Book> findAll(Pageable pageable) {
//        return bookRepository.findAll(pageable);
//    }
//
//    @Override
//    public Optional<Book> findById(Long id) {
//        return bookRepository.findById(id);
//    }
//
//    @Override
//    public Book save(Book book) {
//        return bookRepository.save(book);
//    }
//
//    @Override
//    public void deleteById(Long id) {
//        bookRepository.deleteById(id);
//    }
//
//    @Override
//    public boolean existsById(Long id) {
//        return bookRepository.existsById(id);
//    }
//
//    @Override
//    public Page<Book> findByTitleContaining(String title, Pageable pageable) {
//        return bookRepository.findByTitleContaining(title, pageable);
//    }
//
//    @Override
//    public Page<Book> findByAuthorContaining(String author, Pageable pageable) {
//        return bookRepository.findByAuthorContaining(author, pageable);
//    }
//
//    @Override
//    public Page<Book> findByPublisherContaining(String publisher, Pageable pageable) {
//        return bookRepository.findByPublisherContaining(publisher, pageable);
//    }
//
//    @Override
//    public Page<Book> findByCategory(Category category, Pageable pageable) {
//        return bookRepository.findByCategory(category, pageable);
//    }
//
//    @Override
//    public Page<Book> findByStatus(BookStatus status, Pageable pageable) {
//        return bookRepository.findByStatus(status, pageable);
//    }
//
//    @Override
//    public Page<Book> findByStockQuantityLessThanEqual(Integer stockQuantity, Pageable pageable) {
//        return bookRepository.findByStockQuantityLessThanEqual(stockQuantity, pageable);
//    }
//
//    @Override
//    public Page<Book> findByRegisteredAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable) {
//        return bookRepository.findByRegisteredAtBetween(start, end, pageable);
//    }
//
//    @Override
//    public List<Book> findBestSellers(Pageable pageable) {
//        return bookRepository.findBestSellers(pageable);
//    }
//
//    @Override
//    public List<Book> findNewReleases(LocalDateTime since, Pageable pageable) {
//        return bookRepository.findNewReleases(since, pageable);
//    }
//
//    @Override
//    public Page<Book> searchBooks(String keyword, Pageable pageable) {
//        // 복합 검색 로직 (제목, 저자, 출판사에서 검색)
//        return bookRepository.findByTitleContainingOrAuthorContainingOrPublisherContaining(
//                keyword, keyword, keyword, pageable);
//    }
//
//    @Override
//    public List<Book> findLowStockBooks(int threshold) {
//        return bookRepository.findByStockQuantityLessThanEqual(threshold, Pageable.unpaged()).getContent();
//    }
//
//    @Override
//    public long countByStatus(BookStatus status) {
//        return bookRepository.countByStatus(status);
//    }
//}
