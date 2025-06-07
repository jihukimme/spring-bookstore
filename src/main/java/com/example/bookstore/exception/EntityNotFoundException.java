package com.example.bookstore.exception;

public class EntityNotFoundException extends BookstoreException {
    
    public EntityNotFoundException(String entityName, Object id) {
        super(String.format("%s not found with id: %s", entityName, id));
    }
    
    public EntityNotFoundException(String message) {
        super(message);
    }
}
