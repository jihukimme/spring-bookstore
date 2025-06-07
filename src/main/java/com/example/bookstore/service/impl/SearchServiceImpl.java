package com.example.bookstore.service.impl;

import com.example.bookstore.entity.SearchTerm;
import com.example.bookstore.repository.SearchTermRepository;
import com.example.bookstore.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    
    private final SearchTermRepository searchTermRepository;
    
    @Override
    @Transactional
    public void recordSearch(String term) {
        if (term == null || term.trim().isEmpty()) {
            return;
        }
        
        String cleanTerm = term.trim().toLowerCase();
        Optional<SearchTerm> existingTerm = searchTermRepository.findByTerm(cleanTerm);
        
        if (existingTerm.isPresent()) {
            SearchTerm searchTerm = existingTerm.get();
            searchTerm.setCount(searchTerm.getCount() + 1);
            searchTermRepository.save(searchTerm);
        } else {
            SearchTerm newTerm = new SearchTerm();
            newTerm.setTerm(cleanTerm);
            newTerm.setCount(1);
            searchTermRepository.save(newTerm);
        }
        
        log.debug("Search term recorded: {}", cleanTerm);
    }
    
    @Override
    public List<SearchTerm> getTopSearchTerms(int limit) {
        return searchTermRepository.findTopSearchTerms(PageRequest.of(0, limit));
    }
    
    @Override
    @Transactional
    public void cleanupOldSearchTerms(int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        // Implementation would depend on having a lastSearched field and appropriate repository method
        log.info("Cleanup old search terms older than {} days", daysOld);
    }
}
