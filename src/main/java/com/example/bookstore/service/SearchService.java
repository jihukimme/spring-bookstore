package com.example.bookstore.service;

import com.example.bookstore.entity.SearchTerm;
import com.example.bookstore.repository.SearchTermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SearchService {
    
    private final SearchTermRepository searchTermRepository;
    
    @Transactional
    public void recordSearch(String term) {
        Optional<SearchTerm> existingTerm = searchTermRepository.findByTerm(term);
        
        if (existingTerm.isPresent()) {
            SearchTerm searchTerm = existingTerm.get();
            searchTerm.setCount(searchTerm.getCount() + 1);
            searchTermRepository.save(searchTerm);
        } else {
            SearchTerm newTerm = new SearchTerm();
            newTerm.setTerm(term);
            newTerm.setCount(1);
            searchTermRepository.save(newTerm);
        }
    }
    
    public List<SearchTerm> getTopSearchTerms(int limit) {
        return searchTermRepository.findTopSearchTerms(PageRequest.of(0, limit));
    }
}
