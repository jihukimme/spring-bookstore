package com.example.bookstore.controller;

import com.example.bookstore.dto.CartItemDto;
import com.example.bookstore.dto.UserDto;
import com.example.bookstore.service.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    
    private final CartService cartService;
    
    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        UserDto userDto = (UserDto) session.getAttribute("user");
        if (userDto == null) {
            return "redirect:/login";
        }
        
        List<CartItemDto> cartItems = cartService.getCartItems(userDto);
        model.addAttribute("cartItems", cartItems);
        
        return "cart/view";
    }
    
    @PostMapping("/add")
    public String addToCart(HttpSession session,
                           @RequestParam Long bookId,
                           @RequestParam(defaultValue = "1") Integer quantity) {
        
        UserDto userDto = (UserDto) session.getAttribute("user");
        if (userDto == null) {
            return "redirect:/login";
        }
        
        cartService.addToCart(userDto, bookId, quantity);
        
        return "redirect:/cart";
    }
    
    @PostMapping("/update")
    public String updateCartItem(@RequestParam Long cartItemId,
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
    public String clearCart(HttpSession session) {
        UserDto userDto = (UserDto) session.getAttribute("user");
        if (userDto != null) {
            cartService.clearCart(userDto);
        }
        
        return "redirect:/cart";
    }
}
