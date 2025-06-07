package com.example.bookstore.constants;

public final class BookstoreConstants {
    
    private BookstoreConstants() {
        // Utility class
    }
    
    // Pagination
    public static final int DEFAULT_PAGE_SIZE = 12;
    public static final int ADMIN_PAGE_SIZE = 30;
    public static final int MAX_PAGE_SIZE = 100;
    
    // Search
    public static final int POPULAR_SEARCH_LIMIT = 10;
    public static final int BESTSELLER_LIMIT = 10;
    public static final int NEW_RELEASE_LIMIT = 10;
    
    // Session
    public static final String SESSION_USER_KEY = "user";
    public static final String SESSION_CART_COUNT_KEY = "cartItemCount";
    
    // Validation
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 20;
    public static final int MIN_USER_ID_LENGTH = 4;
    public static final int MAX_USER_ID_LENGTH = 20;
    
    // File Upload
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    public static final String[] ALLOWED_IMAGE_TYPES = {"jpg", "jpeg", "png", "gif"};
    public static final String[] ALLOWED_DOCUMENT_TYPES = {"pdf"};
    
    // Cache
    public static final String CACHE_BESTSELLERS = "bestsellers";
    public static final String CACHE_NEW_RELEASES = "newReleases";
    public static final String CACHE_POPULAR_SEARCHES = "popularSearches";
    
    // Messages
    public static final String MSG_SUCCESS_REGISTER = "회원가입이 완료되었습니다. 로그인해주세요.";
    public static final String MSG_SUCCESS_LOGIN = "로그인되었습니다.";
    public static final String MSG_SUCCESS_LOGOUT = "성공적으로 로그아웃되었습니다.";
    public static final String MSG_ERROR_LOGIN = "아이디 또는 비밀번호가 올바르지 않습니다.";
    public static final String MSG_ERROR_DUPLICATE_USER_ID = "이미 사용 중인 아이디입니다.";
    public static final String MSG_ERROR_DUPLICATE_EMAIL = "이미 사용 중인 이메일입니다.";
}
