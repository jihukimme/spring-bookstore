package com.example.bookstore.controller.admin;

import com.example.bookstore.dto.OrderDto;
import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Order;
import com.example.bookstore.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {
    
    private final OrderService orderService;
    
    @GetMapping
    public String listOrders(
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String bookTitle,
            @RequestParam(required = false) String publisher,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String bookStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model) {
        
        Sort sort = Sort.by(sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<OrderDto> orders = orderService.searchOrders(userName, bookTitle, publisher, author, bookStatus, startDate, endDate, pageable);
        
        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orders.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("userName", userName);
        model.addAttribute("bookTitle", bookTitle);
        model.addAttribute("publisher", publisher);
        model.addAttribute("author", author);
        model.addAttribute("bookStatus", bookStatus);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("pageSize", size);
        model.addAttribute("bookStatuses", Book.BookStatus.values());
        model.addAttribute("orderStatuses", Order.OrderStatus.values());
        
        return "admin/orders/list";
    }
    
    @GetMapping("/{id}")
    public String viewOrder(@PathVariable Long id, Model model) {
        try {
            OrderDto order = orderService.findById(id);
            model.addAttribute("order", order);
            model.addAttribute("statuses", Order.OrderStatus.values());
            
            return "admin/orders/view";
        } catch (Exception e) {
            return "redirect:/admin/orders";
        }
    }
    
    @PostMapping("/{id}/status")
    public String updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status,
            RedirectAttributes redirectAttributes) {
        
        try {
            orderService.updateOrderStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "주문 상태가 성공적으로 업데이트되었습니다.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/admin/orders/" + id;
    }
}
