package com.example.bookstore.controller;

import com.example.bookstore.entity.User;
import com.example.bookstore.service.ReviewService;
import com.example.bookstore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    
    private final ReviewService reviewService;
    private final UserService userService;
    
    @PostMapping("/add")
    public String addReview(
            Authentication authentication,
            @RequestParam Long bookId,
            @RequestParam Integer rating,
            @RequestParam String content) {
        
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByUserId(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            reviewService.addReview(user, bookId, rating, content);
            
            return "redirect:/books/" + bookId;
        } else {
            return "redirect:/login";
        }
    }
    
    @PostMapping("/{id}/delete")
    public String deleteReview(
            Authentication authentication,
            @PathVariable Long id,
            @RequestParam Long bookId) {
        
        if (authentication != null) {
            // In a real application, you would check if the review belongs to the authenticated user
            reviewService.deleteReview(id);
        }
        
        return "redirect:/books/" + bookId;
    }
}
