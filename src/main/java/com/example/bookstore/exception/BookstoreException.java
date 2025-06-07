package com.example.bookstore.exception;

public class BookstoreException extends RuntimeException {
    
    public BookstoreException(String message) {
        super(message);
    }
    
    public BookstoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
