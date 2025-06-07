package com.example.bookstore.util;

import com.example.bookstore.constants.BookstoreConstants;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PaginationUtils {
    
    private PaginationUtils() {
        // Utility class
    }
    
    public static Pageable createPageable(int page, int size, String sortBy, String sortDir) {
        size = Math.min(size, BookstoreConstants.MAX_PAGE_SIZE);
        size = Math.max(size, 1);
        page = Math.max(page, 0);
        
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        
        Sort sort = Sort.by(direction, sortBy);
        return PageRequest.of(page, size, sort);
    }
    
    public static Pageable createDefaultPageable(int page) {
        return createPageable(page, BookstoreConstants.DEFAULT_PAGE_SIZE, "id", "desc");
    }
    
    public static Pageable createAdminPageable(int page, String sortBy, String sortDir) {
        return createPageable(page, BookstoreConstants.ADMIN_PAGE_SIZE, sortBy, sortDir);
    }
}
