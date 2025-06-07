package com.example.bookstore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class SearchTerm {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String term;
    private Integer count;
    private LocalDateTime lastSearched;
    
    @PrePersist
    protected void onCreate() {
        lastSearched = LocalDateTime.now();
        if (count == null) {
            count = 1;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastSearched = LocalDateTime.now();
    }
}
