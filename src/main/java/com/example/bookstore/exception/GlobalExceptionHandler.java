package com.example.bookstore.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFoundException(EntityNotFoundException ex, 
                                               RedirectAttributes redirectAttributes) {
        log.error("Entity not found: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/";
    }
    
    @ExceptionHandler(DuplicateEntityException.class)
    public String handleDuplicateEntityException(DuplicateEntityException ex,
                                                RedirectAttributes redirectAttributes) {
        log.error("Duplicate entity: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/";
    }
    
    @ExceptionHandler(InsufficientStockException.class)
    public String handleInsufficientStockException(InsufficientStockException ex,
                                                  RedirectAttributes redirectAttributes) {
        log.error("Insufficient stock: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/cart";
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationException(MethodArgumentNotValidException ex,
                                           RedirectAttributes redirectAttributes) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        log.error("Validation error: {}", errorMessage);
        redirectAttributes.addFlashAttribute("error", errorMessage);
        return "redirect:/";
    }
    
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        log.error("Unexpected error occurred", ex);
        model.addAttribute("error", "시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        return "error/500";
    }
}
