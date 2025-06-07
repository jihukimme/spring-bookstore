package com.example.bookstore.controller.admin;

import com.example.bookstore.service.BookService;
import com.example.bookstore.service.OrderService;
import com.example.bookstore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {
    
    private final BookService bookService;
    private final OrderService orderService;
    private final UserService userService;
    
    @GetMapping
    public String dashboard(Model model) {
        // Get statistics for dashboard
        long totalBooks = bookService.findAllBooks(PageRequest.of(0, 1)).getTotalElements();
        long totalOrders = orderService.findAllOrders(PageRequest.of(0, 1)).getTotalElements();
        long totalUsers = userService.findAllUsers(PageRequest.of(0, 1)).getTotalElements();
        
        model.addAttribute("totalBooks", totalBooks);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalUsers", totalUsers);
        
        return "admin/dashboard";
    }
}
