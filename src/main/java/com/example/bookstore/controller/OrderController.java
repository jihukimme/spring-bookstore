package com.example.bookstore.controller;

import com.example.bookstore.dto.CartItemDto;
import com.example.bookstore.dto.OrderDto;
import com.example.bookstore.dto.OrderItemDto;
import com.example.bookstore.dto.UserDto;
import com.example.bookstore.service.CartService;
import com.example.bookstore.service.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    private final CartService cartService;
    
    @GetMapping
    public String listOrders(HttpSession session,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            Model model) {
        
        UserDto userDto = (UserDto) session.getAttribute("user");
        if (userDto == null) {
            return "redirect:/login";
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderDto> orders = orderService.findByUser(userDto, pageable);
        
        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orders.getTotalPages());
        
        return "orders/list";
    }
    
    @GetMapping("/{id}")
    public String viewOrder(HttpSession session,
                           @PathVariable Long id,
                           Model model) {
        
        UserDto userDto = (UserDto) session.getAttribute("user");
        if (userDto == null) {
            return "redirect:/login";
        }
        
        try {
            OrderDto order = orderService.findById(id);
            
            // Check if the order belongs to the authenticated user
            if (order.getUserId().equals(userDto.getId())) {
                model.addAttribute("order", order);
                return "orders/view";
            }
        } catch (Exception e) {
            // Order not found or access denied
        }
        
        return "redirect:/orders";
    }
    
    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        UserDto userDto = (UserDto) session.getAttribute("user");
        if (userDto == null) {
            return "redirect:/login";
        }
        
        List<CartItemDto> cartItems = cartService.getCartItems(userDto);
        
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }
        
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("user", userDto);
        
        return "orders/checkout";
    }
    
    @PostMapping("/place")
    public String placeOrder(HttpSession session,
                            @RequestParam String shippingAddress,
                            @RequestParam String paymentMethod,
                            @RequestParam String paymentId) {
        
        UserDto userDto = (UserDto) session.getAttribute("user");
        if (userDto == null) {
            return "redirect:/login";
        }
        
        List<CartItemDto> cartItems = cartService.getCartItems(userDto);
        
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }
        
        List<OrderItemDto> orderItems = new ArrayList<>();
        
        for (CartItemDto cartItem : cartItems) {
            OrderItemDto orderItem = OrderItemDto.builder()
                .bookId(cartItem.getBookId())
                .quantity(cartItem.getQuantity())
                .price(cartItem.getBookPrice())
                .build();
            orderItems.add(orderItem);
        }
        
        OrderDto order = orderService.createOrder(userDto, orderItems, shippingAddress, paymentMethod, paymentId);
        
        // Clear the cart after successful order
        cartService.clearCart(userDto);
        
        return "redirect:/orders/" + order.getId();
    }
    
    @PostMapping("/{id}/cancel")
    public String cancelOrder(HttpSession session,
                             @PathVariable Long id) {
        
        UserDto userDto = (UserDto) session.getAttribute("user");
        if (userDto == null) {
            return "redirect:/login";
        }
        
        try {
            OrderDto order = orderService.findById(id);
            
            // Check if the order belongs to the authenticated user
            if (order.getUserId().equals(userDto.getId())) {
                orderService.cancelOrder(id);
            }
        } catch (RuntimeException e) {
            // Handle exception, maybe add a flash message
        }
        
        return "redirect:/orders";
    }
}
