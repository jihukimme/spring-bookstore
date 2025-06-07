package com.example.bookstore.controller.admin;

import com.example.bookstore.entity.Book;
import com.example.bookstore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/admin/books")
@RequiredArgsConstructor
public class AdminBookController {
    
    private final BookService bookService;
    
    @GetMapping
    public String listBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String publisher,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Integer stockQuantity,
            @RequestParam(required = false) Book.BookStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {
        
        Sort sort = Sort.by(sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Book> books = bookService.searchBooks(title, publisher, author, stockQuantity, status, startDate, endDate, pageable);
        
        model.addAttribute("books", books);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", books.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("title", title);
        model.addAttribute("publisher", publisher);
        model.addAttribute("author", author);
        model.addAttribute("stockQuantity", stockQuantity);
        model.addAttribute("status", status);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("pageSize", size);
        
        return "admin/books/list";
    }
    
    @GetMapping("/new")
    public String newBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("statuses", Book.BookStatus.values());
        
        return "admin/books/form";
    }
    
    @PostMapping
    public String saveBook(
            @ModelAttribute Book book,
            @RequestParam(required = false) MultipartFile imageFile,
            @RequestParam(required = false) MultipartFile pdfFile,
            RedirectAttributes redirectAttributes) {
        
        // In a real application, you would handle file uploads here
        // For this example, we'll just save the book
        
        bookService.saveBook(book);
        redirectAttributes.addFlashAttribute("success", "도서가 성공적으로 저장되었습니다.");
        
        return "redirect:/admin/books";
    }
    
    @GetMapping("/{id}/edit")
    public String editBookForm(@PathVariable Long id, Model model) {
        Optional<Book> bookOpt = bookService.findById(id);
        
        if (bookOpt.isPresent()) {
            model.addAttribute("book", bookOpt.get());
            model.addAttribute("statuses", Book.BookStatus.values());
            
            return "admin/books/form";
        } else {
            return "redirect:/admin/books";
        }
    }
    
    @GetMapping("/{id}")
    public String viewBook(@PathVariable Long id, Model model) {
        Optional<Book> bookOpt = bookService.findById(id);
        
        if (bookOpt.isPresent()) {
            model.addAttribute("book", bookOpt.get());
            
            return "admin/books/view";
        } else {
            return "redirect:/admin/books";
        }
    }
    
    @PostMapping("/{id}/delete")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        bookService.deleteBook(id);
        redirectAttributes.addFlashAttribute("success", "도서가 성공적으로 삭제되었습니다.");
        
        return "redirect:/admin/books";
    }
}
