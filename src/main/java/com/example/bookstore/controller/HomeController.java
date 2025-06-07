package com.example.bookstore.controller;

import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.SearchTerm;
import com.example.bookstore.service.BookService;
import com.example.bookstore.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
    
    private final BookService bookService;
    private final SearchService searchService;
    
    @GetMapping("/")
    public String home(Model model) {
        List<Book> bestSellers = bookService.findBestSellers(10);
        List<Book> newReleases = bookService.findNewReleases(10);
        List<SearchTerm> popularSearches = searchService.getTopSearchTerms(10);
        
        model.addAttribute("bestSellers", bestSellers);
        model.addAttribute("newReleases", newReleases);
        model.addAttribute("popularSearches", popularSearches);
        
        return "home";
    }
}
