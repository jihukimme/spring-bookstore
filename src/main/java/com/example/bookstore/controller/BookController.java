package com.example.bookstore.controller;

import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Category;
import com.example.bookstore.entity.Review;
import com.example.bookstore.service.BookService;
import com.example.bookstore.service.ReviewService;
import com.example.bookstore.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
        
        Page<Book> books;
        
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
    public String viewBook(@PathVariable Long id, Model model) {
        Optional<Book> bookOpt = bookService.findById(id);
        
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            List<Review> reviews = reviewService.findByBook(book);
            
            model.addAttribute("book", book);
            model.addAttribute("reviews", reviews);
            
            return "books/view";
        } else {
            return "redirect:/books";
        }
    }
    
    @GetMapping("/category/{categoryId}")
    public String listBooksByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {
        
        // This would need a CategoryService to fetch the category
        Category category = new Category(); // Placeholder
        category.setId(categoryId);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> books = bookService.findByCategory(category, pageable);
        
        model.addAttribute("books", books);
        model.addAttribute("category", category);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", books.getTotalPages());
        
        return "books/category";
    }
}
