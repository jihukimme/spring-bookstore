package com.example.bookstore.exception;

public class DuplicateEntityException extends BookstoreException {
    
    public DuplicateEntityException(String entityName, String field, Object value) {
        super(String.format("%s already exists with %s: %s", entityName, field, value));
    }
    
    public DuplicateEntityException(String message) {
        super(message);
    }
}
