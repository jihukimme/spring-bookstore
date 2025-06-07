package com.example.bookstore.service;

import com.example.bookstore.entity.SearchTerm;

import java.util.List;

public interface SearchService {

    void recordSearch(String term);
    
    List<SearchTerm> getTopSearchTerms(int limit);

    void cleanupOldSearchTerms(int daysOld);
}
