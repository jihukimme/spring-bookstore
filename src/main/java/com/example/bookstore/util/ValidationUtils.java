package com.example.bookstore.util;

import java.util.regex.Pattern;

public final class ValidationUtils {
    
    private ValidationUtils() {
        // Utility class
    }
    
    private static final Pattern USER_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{3}-\\d{4}-\\d{4}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern ISBN_PATTERN = Pattern.compile("^\\d{13}$");
    
    public static boolean isValidUserId(String userId) {
        return userId != null && 
               userId.length() >= 4 && 
               userId.length() <= 20 && 
               USER_ID_PATTERN.matcher(userId).matches();
    }
    
    public static boolean isValidPassword(String password) {
        return password != null && 
               password.length() >= 8 && 
               password.length() <= 20;
    }
    
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
    
    public static boolean isValidIsbn(String isbn) {
        return isbn != null && ISBN_PATTERN.matcher(isbn).matches();
    }
    
    public static String sanitizeSearchTerm(String term) {
        if (term == null) {
            return "";
        }
        return term.trim().replaceAll("[<>\"'&]", "");
    }
}
