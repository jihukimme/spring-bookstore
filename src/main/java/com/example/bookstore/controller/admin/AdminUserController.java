package com.example.bookstore.controller.admin;

import com.example.bookstore.dto.UserDto;
import com.example.bookstore.entity.User;
import com.example.bookstore.service.UserService;
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
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    
    private final UserService userService;
    
    @GetMapping
    public String listUsers(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "joinDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model) {
        
        Sort sort = Sort.by(sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<UserDto> users = userService.searchUsers(userId, status, email, startDate, endDate, grade, name, pageable);
        
        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", users.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("userId", userId);
        model.addAttribute("status", status);
        model.addAttribute("email", email);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("grade", grade);
        model.addAttribute("name", name);
        model.addAttribute("pageSize", size);
        model.addAttribute("statuses", User.UserStatus.values());
        model.addAttribute("grades", User.UserGrade.values());
        
        return "admin/users/list";
    }
    
    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        Optional<UserDto> userOpt = userService.findByIdOptional(id);
        
        if (userOpt.isPresent()) {
            model.addAttribute("user", userOpt.get());
            model.addAttribute("statuses", User.UserStatus.values());
            model.addAttribute("grades", User.UserGrade.values());
            
            return "admin/users/view";
        } else {
            return "redirect:/admin/users";
        }
    }
    
    @PostMapping("/{id}/status")
    public String updateUserStatus(
            @PathVariable Long id,
            @RequestParam String status,
            RedirectAttributes redirectAttributes) {
        
        Optional<UserDto> userOpt = userService.findByIdOptional(id);
        
        if (userOpt.isPresent()) {
            UserDto userDto = userOpt.get();
            userDto.setStatus(status);
            userService.updateUser(userDto);
            
            redirectAttributes.addFlashAttribute("success", "회원 상태가 성공적으로 업데이트되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("error", "회원을 찾을 수 없습니다.");
        }
        
        return "redirect:/admin/users/" + id;
    }
    
    @PostMapping("/{id}/grade")
    public String updateUserGrade(
            @PathVariable Long id,
            @RequestParam String grade,
            RedirectAttributes redirectAttributes) {
        
        Optional<UserDto> userOpt = userService.findByIdOptional(id);
        
        if (userOpt.isPresent()) {
            UserDto userDto = userOpt.get();
            userDto.setGrade(grade);
            userService.updateUser(userDto);
            
            redirectAttributes.addFlashAttribute("success", "회원 등급이 성공적으로 업데이트되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("error", "회원을 찾을 수 없습니다.");
        }
        
        return "redirect:/admin/users/" + id;
    }
}
