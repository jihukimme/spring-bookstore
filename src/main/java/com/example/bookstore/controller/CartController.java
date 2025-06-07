package com.example.bookstore.controller;

import com.example.bookstore.entity.CartItem;
import com.example.bookstore.entity.User;
import com.example.bookstore.service.CartService;
import com.example.bookstore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    
    private final CartService cartService;
    private final UserService userService;
    
    @GetMapping
    public String viewCart(Authentication authentication, Model model) {
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByUserId(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            List<CartItem> cartItems = cartService.getCartItems(user);
            model.addAttribute("cartItems", cartItems);
            
            return "cart/view";
        } else {
            return "redirect:/login";
        }
    }
    
    @PostMapping("/add")
    public String addToCart(
            Authentication authentication,
            @RequestParam Long bookId,
            @RequestParam(defaultValue = "1") Integer quantity) {
        
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByUserId(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            cartService.addToCart(user, bookId, quantity);
            
            return "redirect:/cart";
        } else {
            return "redirect:/login";
        }
    }
    
    @PostMapping("/update")
    public String updateCartItem(
            @RequestParam Long cartItemId,
            @RequestParam Integer quantity) {
        
        cartService.updateCartItemQuantity(cartItemId, quantity);
        
        return "redirect:/cart";
    }
    
    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long cartItemId) {
        cartService.removeFromCart(cartItemId);
        
        return "redirect:/cart";
    }
    
    @PostMapping("/clear")
    public String clearCart(Authentication authentication) {
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByUserId(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            cartService.clearCart(user);
        }
        
        return "redirect:/cart";
    }
}
