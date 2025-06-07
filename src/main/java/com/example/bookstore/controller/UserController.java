package com.example.bookstore.controller;

import com.example.bookstore.constants.BookstoreConstants;
import com.example.bookstore.dto.UserDto;
import com.example.bookstore.service.UserService;
import com.example.bookstore.util.ValidationUtils;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new UserDto());
        return "user/register";
    }
    
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") UserDto userDto,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            return "user/register";
        }
        
        try {
            userService.registerUser(userDto);
            redirectAttributes.addFlashAttribute("success", BookstoreConstants.MSG_SUCCESS_REGISTER);
            return "redirect:/login";
        } catch (Exception e) {
            log.error("Registration failed for user: {}", userDto.getUserId(), e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }
    
    @GetMapping("/login")
    public String loginForm(HttpSession session) {
        if (session.getAttribute(BookstoreConstants.SESSION_USER_KEY) != null) {
            return "redirect:/";
        }
        return "user/login";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam String username, 
                       @RequestParam String password,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {
        
        try {
            UserDto user = userService.authenticate(username, password);
            
            if (user != null) {
                session.setAttribute(BookstoreConstants.SESSION_USER_KEY, user);
                log.info("User logged in: {}", username);
                return "redirect:/";
            } else {
                redirectAttributes.addFlashAttribute("error", BookstoreConstants.MSG_ERROR_LOGIN);
                return "redirect:/login";
            }
        } catch (Exception e) {
            log.error("Login failed for user: {}", username, e);
            redirectAttributes.addFlashAttribute("error", BookstoreConstants.MSG_ERROR_LOGIN);
            return "redirect:/login";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", BookstoreConstants.MSG_SUCCESS_LOGOUT);
        return "redirect:/";
    }
    
    @GetMapping("/mypage")
    public String myPage(HttpSession session, Model model) {
        UserDto user = (UserDto) session.getAttribute(BookstoreConstants.SESSION_USER_KEY);
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        return "user/mypage";
    }
    
    @GetMapping("/forgot-password")
    public String forgotPasswordForm() {
        return "user/forgot-password";
    }
    
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, 
                                       RedirectAttributes redirectAttributes) {
        
        if (!ValidationUtils.isValidEmail(email)) {
            redirectAttributes.addFlashAttribute("error", "올바른 이메일 형식이 아닙니다.");
            return "redirect:/forgot-password";
        }
        
        if (!userService.existsByEmail(email)) {
            redirectAttributes.addFlashAttribute("error", "등록되지 않은 이메일입니다.");
            return "redirect:/forgot-password";
        }
        
        // TODO: 실제 비밀번호 재설정 이메일 발송 로직 구현
        redirectAttributes.addFlashAttribute("success", "비밀번호 재설정 링크가 이메일로 전송되었습니다.");
        return "redirect:/login";
    }
    
    @GetMapping("/check-userid")
    @ResponseBody
    public boolean checkUserIdAvailability(@RequestParam String userId) {
        if (!ValidationUtils.isValidUserId(userId)) {
            return false;
        }
        return !userService.existsByUserId(userId);
    }
}
