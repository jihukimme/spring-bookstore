package com.example.bookstore.controller;

import com.example.bookstore.entity.CartItem;
import com.example.bookstore.entity.Order;
import com.example.bookstore.entity.OrderItem;
import com.example.bookstore.entity.User;
import com.example.bookstore.service.CartService;
import com.example.bookstore.service.OrderService;
import com.example.bookstore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    private final UserService userService;
    private final CartService cartService;
    
    @GetMapping
    public String listOrders(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByUserId(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Pageable pageable = PageRequest.of(page, size);
            Page<Order> orders = orderService.findByUser(user, pageable);
            
            model.addAttribute("orders", orders);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", orders.getTotalPages());
            
            return "orders/list";
        } else {
            return "redirect:/login";
        }
    }
    
    @GetMapping("/{id}")
    public String viewOrder(
            Authentication authentication,
            @PathVariable Long id,
            Model model) {
        
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByUserId(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Optional<Order> orderOpt = orderService.findById(id);
            
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                
                // Check if the order belongs to the authenticated user
                if (order.getUser().getId().equals(user.getId())) {
                    model.addAttribute("order", order);
                    return "orders/view";
                }
            }
            
            return "redirect:/orders";
        } else {
            return "redirect:/login";
        }
    }
    
    @GetMapping("/checkout")
    public String checkout(Authentication authentication, Model model) {
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByUserId(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            List<CartItem> cartItems = cartService.getCartItems(user);
            
            if (cartItems.isEmpty()) {
                return "redirect:/cart";
            }
            
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("user", user);
            
            return "orders/checkout";
        } else {
            return "redirect:/login";
        }
    }
    
    @PostMapping("/place")
    public String placeOrder(
            Authentication authentication,
            @RequestParam String shippingAddress,
            @RequestParam String paymentMethod,
            @RequestParam String paymentId) {
        
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByUserId(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            List<CartItem> cartItems = cartService.getCartItems(user);
            
            if (cartItems.isEmpty()) {
                return "redirect:/cart";
            }
            
            List<OrderItem> orderItems = new ArrayList<>();
            
            for (CartItem cartItem : cartItems) {
                OrderItem orderItem = new OrderItem();
                orderItem.setBook(cartItem.getBook());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPrice(cartItem.getBook().getPrice());
                orderItem.setBookStatus(cartItem.getBook().getStatus());
                orderItems.add(orderItem);
            }
            
            Order order = orderService.createOrder(user, orderItems, shippingAddress, paymentMethod, paymentId);
            
            // Clear the cart after successful order
            cartService.clearCart(user);
            
            return "redirect:/orders/" + order.getId();
        } else {
            return "redirect:/login";
        }
    }
    
    @PostMapping("/{id}/cancel")
    public String cancelOrder(
            Authentication authentication,
            @PathVariable Long id) {
        
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByUserId(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Optional<Order> orderOpt = orderService.findById(id);
            
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                
                // Check if the order belongs to the authenticated user
                if (order.getUser().getId().equals(user.getId())) {
                    try {
                        orderService.cancelOrder(id);
                    } catch (RuntimeException e) {
                        // Handle exception, maybe add a flash message
                    }
                }
            }
            
            return "redirect:/orders";
        } else {
            return "redirect:/login";
        }
    }
}
