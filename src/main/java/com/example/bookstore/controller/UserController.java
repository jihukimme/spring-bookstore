package com.example.bookstore.controller;

import com.example.bookstore.entity.User;
import com.example.bookstore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "user/register";
    }
    
    @PostMapping("/register")
    public String register(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        // Check if userId already exists
        if (userService.existsByUserId(user.getUserId())) {
            redirectAttributes.addFlashAttribute("error", "이미 사용 중인 아이디입니다.");
            return "redirect:/register";
        }
        
        // Check if email already exists
        if (userService.existsByEmail(user.getEmail())) {
            redirectAttributes.addFlashAttribute("error", "이미 사용 중인 이메일입니다.");
            return "redirect:/register";
        }
        
        userService.registerUser(user);
        redirectAttributes.addFlashAttribute("success", "회원가입이 완료되었습니다. 로그인해주세요.");
        
        return "redirect:/login";
    }
    
    @GetMapping("/login")
    public String loginForm() {
        return "user/login";
    }
    
    @GetMapping("/mypage")
    public String myPage() {
        return "user/mypage";
    }
    
    @GetMapping("/forgot-password")
    public String forgotPasswordForm() {
        return "user/forgot-password";
    }
    
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, RedirectAttributes redirectAttributes) {
        // Check if email exists
        if (!userService.existsByEmail(email)) {
            redirectAttributes.addFlashAttribute("error", "등록되지 않은 이메일입니다.");
            return "redirect:/forgot-password";
        }
        
        // In a real application, you would send a password reset email here
        // For this example, we'll just show a success message
        
        redirectAttributes.addFlashAttribute("success", "비밀번호 재설정 링크가 이메일로 전송되었습니다.");
        return "redirect:/login";
    }
    
    @GetMapping("/check-userid")
    @ResponseBody
    public boolean checkUserIdAvailability(@RequestParam String userId) {
        return !userService.existsByUserId(userId);
    }
}
