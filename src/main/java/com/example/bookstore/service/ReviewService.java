package com.example.bookstore.service;

import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Review;
import com.example.bookstore.entity.User;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    
    public List<Review> findByBook(Book book) {
        return reviewRepository.findByBook(book);
    }
    
    public List<Review> findByUser(User user) {
        return reviewRepository.findByUser(user);
    }
    
    public Page<Review> findByBook(Book book, Pageable pageable) {
        return reviewRepository.findByBook(book, pageable);
    }
    
    @Transactional
    public Review addReview(User user, Long bookId, Integer rating, String content) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        
        Review review = new Review();
        review.setUser(user);
        review.setBook(book);
        review.setRating(rating);
        review.setContent(content);
        
        Review savedReview = reviewRepository.save(review);
        
        // Update book rating
        updateBookRating(book);
        
        return savedReview;
    }
    
    @Transactional
    public void deleteReview(Long reviewId) {
        Optional<Review> reviewOpt = reviewRepository.findById(reviewId);
        if (reviewOpt.isPresent()) {
            Review review = reviewOpt.get();
            Book book = review.getBook();
            
            reviewRepository.delete(review);
            
            // Update book rating
            updateBookRating(book);
        }
    }
    
    private void updateBookRating(Book book) {
        Double avgRating = reviewRepository.getAverageRatingForBook(book);
        if (avgRating != null) {
            book.setRating(avgRating);
            bookRepository.save(book);
        }
    }
}
