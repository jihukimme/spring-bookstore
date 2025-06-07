package com.example.bookstore.dao.impl;

import com.example.bookstore.dao.ReviewDao;
import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Review;
import com.example.bookstore.entity.User;
import com.example.bookstore.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReviewDaoImpl implements ReviewDao {
    
    private final ReviewRepository reviewRepository;
    
    @Override
    public List<Review> findByBook(Book book) {
        return reviewRepository.findByBook(book);
    }
    
    @Override
    public List<Review> findByUser(User user) {
        return reviewRepository.findByUser(user);
    }
    
    @Override
    public Page<Review> findByBook(Book book, Pageable pageable) {
        return reviewRepository.findByBook(book, pageable);
    }
    
    @Override
    public Optional<Review> findById(Long id) {
        return reviewRepository.findById(id);
    }
    
    @Override
    public Review save(Review review) {
        return reviewRepository.save(review);
    }
    
    @Override
    public void delete(Review review) {
        reviewRepository.delete(review);
    }
    
    @Override
    public void deleteById(Long id) {
        reviewRepository.deleteById(id);
    }
    
    @Override
    public Double getAverageRatingForBook(Book book) {
        return reviewRepository.getAverageRatingForBook(book);
    }
    
    @Override
    public long countByBook(Book book) {
        return reviewRepository.findByBook(book).size();
    }
    
    @Override
    public boolean existsByUserAndBook(User user, Book book) {
        return reviewRepository.findByUser(user).stream()
                .anyMatch(review -> review.getBook().getId().equals(book.getId()));
    }
}
