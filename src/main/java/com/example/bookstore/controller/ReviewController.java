package com.example.bookstore.controller;

import com.example.bookstore.dto.ReviewDto;
import com.example.bookstore.dto.UserDto;
import com.example.bookstore.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    
    private final ReviewService reviewService;
    
    @PostMapping("/add")
    public String addReview(HttpSession session,
                           @Valid @ModelAttribute ReviewDto reviewDto,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        
        UserDto userDto = (UserDto) session.getAttribute("user");
        if (userDto == null) {
            return "redirect:/login";
        }
        
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "리뷰 작성 중 오류가 발생했습니다.");
            return "redirect:/books/" + reviewDto.getBookId();
        }
        
        try {
            reviewService.addReview(userDto, reviewDto.getBookId(), reviewDto.getRating(), reviewDto.getContent());
            redirectAttributes.addFlashAttribute("success", "리뷰가 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            log.error("Failed to add review", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/books/" + reviewDto.getBookId();
    }
    
    @PostMapping("/{id}/delete")
    public String deleteReview(HttpSession session,
                              @PathVariable Long id,
                              @RequestParam Long bookId,
                              RedirectAttributes redirectAttributes) {
        
        UserDto userDto = (UserDto) session.getAttribute("user");
        if (userDto == null) {
            return "redirect:/login";
        }
        
        try {
            reviewService.deleteReview(id);
            redirectAttributes.addFlashAttribute("success", "리뷰가 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            log.error("Failed to delete review", e);
            redirectAttributes.addFlashAttribute("error", "리뷰 삭제 중 오류가 발생했습니다.");
        }
        
        return "redirect:/books/" + bookId;
    }
    
    @GetMapping("/my")
    public String myReviews(HttpSession session, Model model) {
        UserDto userDto = (UserDto) session.getAttribute("user");
        if (userDto == null) {
            return "redirect:/login";
        }
        
        try {
            List<ReviewDto> reviews = reviewService.findByUser(userDto);
            model.addAttribute("reviews", reviews);
            
            return "reviews/my-reviews";
        } catch (Exception e) {
            log.error("Failed to load user reviews", e);
            return "redirect:/";
        }
    }
    
    @GetMapping("/book/{bookId}")
    @ResponseBody
    public List<ReviewDto> getBookReviews(@PathVariable Long bookId) {
        return reviewService.findByBookId(bookId);
    }
    
    @GetMapping("/book/{bookId}/can-review")
    @ResponseBody
    public boolean canUserReview(HttpSession session, @PathVariable Long bookId) {
        UserDto userDto = (UserDto) session.getAttribute("user");
        if (userDto == null) {
            return false;
        }
        
        try {
            return reviewService.canUserReview(userDto, bookId);
        } catch (Exception e) {
            log.error("Failed to check review permission", e);
            return false;
        }
    }
}
