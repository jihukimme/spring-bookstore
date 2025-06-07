package com.example.bookstore.controller;

import com.example.bookstore.dto.BookDto;
import com.example.bookstore.dto.ReviewDto;
import com.example.bookstore.dto.UserDto;
import com.example.bookstore.service.BookService;
import com.example.bookstore.service.ReviewService;
import com.example.bookstore.service.SearchService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    
    private final BookService bookService;
    private final ReviewService reviewService;
    private final SearchService searchService;
    
    @GetMapping
    public String listBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String publisher,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {
        
        Sort sort = Sort.by(sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<BookDto> books;
        
        if (title != null && !title.isEmpty()) {
            books = bookService.searchBooks(title, null, null, null, null, null, null, pageable);
            searchService.recordSearch(title);
        } else if (author != null && !author.isEmpty()) {
            books = bookService.searchBooks(null, null, author, null, null, null, null, pageable);
            searchService.recordSearch(author);
        } else if (publisher != null && !publisher.isEmpty()) {
            books = bookService.searchBooks(null, publisher, null, null, null, null, null, pageable);
            searchService.recordSearch(publisher);
        } else {
            books = bookService.findAllBooks(pageable);
        }
        
        model.addAttribute("books", books);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", books.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("title", title);
        model.addAttribute("author", author);
        model.addAttribute("publisher", publisher);
        
        return "books/list";
    }
    
    @GetMapping("/{id}")
    public String viewBook(@PathVariable Long id, HttpSession session, Model model) {
        try {
            BookDto book = bookService.findById(id);
            List<ReviewDto> reviews = reviewService.findByBookId(id);
            
            // Check if user can write a review
            UserDto userDto = (UserDto) session.getAttribute("user");
            boolean canReview = false;
            if (userDto != null) {
                canReview = reviewService.canUserReview(userDto, id);
            }
            
            // Get review statistics
            Double avgRating = reviewService.getAverageRatingForBook(id);
            long reviewCount = reviewService.getReviewCountForBook(id);
            
            model.addAttribute("book", book);
            model.addAttribute("reviews", reviews);
            model.addAttribute("canReview", canReview);
            model.addAttribute("avgRating", avgRating);
            model.addAttribute("reviewCount", reviewCount);
            model.addAttribute("newReview", new ReviewDto());
            
            return "books/view";
        } catch (Exception e) {
            return "redirect:/books";
        }
    }
}
