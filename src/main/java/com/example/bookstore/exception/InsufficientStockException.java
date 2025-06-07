package com.example.bookstore.exception;

public class InsufficientStockException extends BookstoreException {
    
    public InsufficientStockException(String bookTitle, int requested, int available) {
        super(String.format("Insufficient stock for book '%s'. Requested: %d, Available: %d", 
                bookTitle, requested, available));
    }
    
    public InsufficientStockException(String message) {
        super(message);
    }
}
