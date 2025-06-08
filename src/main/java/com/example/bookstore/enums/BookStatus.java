package com.example.bookstore.enums;

public enum BookStatus {
    SELLING("판매중"),
    OUT_OF_PRINT("절판"),
    OUT_OF_STOCK("일시품절"),
    COMING_SOON("입고예정");

    private final String displayName;

    BookStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}