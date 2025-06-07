package com.example.bookstore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private BigDecimal price;
    private String imageUrl;
    private String pdfPreviewUrl;
    private String description;
    private String size;
    private Double rating;
    private Integer salesIndex;
    private Integer stockQuantity;
    
    @Enumerated(EnumType.STRING)
    private BookStatus status;
    
    private LocalDateTime registeredAt;
    private LocalDateTime updatedAt;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    
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
    
    @PrePersist
    protected void onCreate() {
        registeredAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
