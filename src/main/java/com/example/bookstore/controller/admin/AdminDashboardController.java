package com.example.bookstore.controller.admin;

import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Order;
import com.example.bookstore.entity.User;
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
        
        // Get counts by status
        long sellingBooks = bookService.getBookCountByStatus(Book.BookStatus.SELLING.name());
        long outOfStockBooks = bookService.getBookCountByStatus(Book.BookStatus.OUT_OF_STOCK.name());
        
        long pendingOrders = orderService.getOrderCountByStatus(Order.OrderStatus.PENDING.name());
        long shippingOrders = orderService.getOrderCountByStatus(Order.OrderStatus.SHIPPING.name());
        
        long activeUsers = userService.getUserCountByStatus(User.UserStatus.ACTIVE.name());
        long vipUsers = userService.getUserCountByGrade(User.UserGrade.VIP.name());
        
        model.addAttribute("totalBooks", totalBooks);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalUsers", totalUsers);
        
        model.addAttribute("sellingBooks", sellingBooks);
        model.addAttribute("outOfStockBooks", outOfStockBooks);
        
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("shippingOrders", shippingOrders);
        
        model.addAttribute("activeUsers", activeUsers);
        model.addAttribute("vipUsers", vipUsers);
        
        return "admin/dashboard";
    }
}
