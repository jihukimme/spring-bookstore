package com.example.bookstore.service.impl;

import com.example.bookstore.dao.BookDao;
import com.example.bookstore.dao.ReviewDao;
import com.example.bookstore.dao.UserDao;
import com.example.bookstore.dto.ReviewDto;
import com.example.bookstore.dto.UserDto;
import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Review;
import com.example.bookstore.entity.User;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    
    private final ReviewDao reviewDao;
    private final BookDao bookDao;
    private final UserDao userDao;
    
    @Override
    public List<ReviewDto> findByBookId(Long bookId) {
        Book book = bookDao.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book", bookId));
        return reviewDao.findByBook(book).stream()
                .map(ReviewDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ReviewDto> findByUser(UserDto userDto) {
        User user = userDao.findById(userDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("User", userDto.getId()));
        return reviewDao.findByUser(user).stream()
                .map(ReviewDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<ReviewDto> findByBookId(Long bookId, Pageable pageable) {
        Book book = bookDao.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book", bookId));
        return reviewDao.findByBook(book, pageable)
                .map(ReviewDto::fromEntity);
    }
    
    @Override
    @Transactional
    public ReviewDto addReview(UserDto userDto, Long bookId, Integer rating, String content) {
        User user = userDao.findById(userDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("User", userDto.getId()));
        
        Book book = bookDao.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book", bookId));
        
        // Check if user already reviewed this book
        if (reviewDao.existsByUserAndBook(user, book)) {
            throw new RuntimeException("이미 이 도서에 대한 리뷰를 작성하셨습니다.");
        }
        
        Review review = new Review();
        review.setUser(user);
        review.setBook(book);
        review.setRating(rating);
        review.setContent(content);
        
        Review savedReview = reviewDao.save(review);
        
        // Update book rating
        updateBookRating(book);
        
        log.info("Review added: Book {} by User {}", book.getTitle(), user.getUserId());
        
        return ReviewDto.fromEntity(savedReview);
    }
    
    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        Optional<Review> reviewOpt = reviewDao.findById(reviewId);
        if (reviewOpt.isPresent()) {
            Review review = reviewOpt.get();
            Book book = review.getBook();
            
            reviewDao.delete(review);
            
            // Update book rating
            updateBookRating(book);
            
            log.info("Review deleted: ID {}", reviewId);
        } else {
            throw new EntityNotFoundException("Review", reviewId);
        }
    }
    
    @Override
    public boolean canUserReview(UserDto userDto, Long bookId) {
        User user = userDao.findById(userDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("User", userDto.getId()));
        
        Book book = bookDao.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book", bookId));
        
        return !reviewDao.existsByUserAndBook(user, book);
    }
    
    @Override
    public Double getAverageRatingForBook(Long bookId) {
        Book book = bookDao.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book", bookId));
        
        return reviewDao.getAverageRatingForBook(book);
    }
    
    @Override
    public long getReviewCountForBook(Long bookId) {
        Book book = bookDao.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book", bookId));
        
        return reviewDao.countByBook(book);
    }
    
    private void updateBookRating(Book book) {
        Double avgRating = reviewDao.getAverageRatingForBook(book);
        if (avgRating != null) {
            book.setRating(avgRating);
            bookDao.save(book);
            log.debug("Book rating updated: {} - New rating: {}", book.getTitle(), avgRating);
        }
    }
}
