package com.example.bookstore.repository;

import com.example.bookstore.entity.SearchTerm;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SearchTermRepository extends JpaRepository<SearchTerm, Long> {
    
    Optional<SearchTerm> findByTerm(String term);
    
    @Query("SELECT s FROM SearchTerm s ORDER BY s.count DESC")
    List<SearchTerm> findTopSearchTerms(Pageable pageable);
}
