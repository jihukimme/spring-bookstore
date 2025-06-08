package com.example.bookstore.entity;

import com.example.bookstore.enums.BookStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data // @Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor 포함
@Entity // JPA 엔티티임을 선언(book이라는 이름의 테이블로 매핑)
@Builder // 객체 생성 시 Book.builder().build() 형태로 생성 가능
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동 증가하도록 설정한 ID 값
    private Long id;
    
    private String isbn;
    private String title;
    private String author; // 저자
    private String publisher; // 출판사
    private Double price;
    private String imageUrl;
    private String description;
    private Double rating; // 평점
    private Integer salesQuantity; // 판매수량
    private Integer stockQuantity; // 재고수량
    
    @Enumerated(EnumType.STRING) // Enum 타입을 DB에 저장할 때, Enum 타입을 String으로 저장
    private BookStatus status;
    
    private LocalDateTime registeredAt; // LocalDateTime 타입 : 날짜 저장
    private LocalDateTime updatedAt;
    
    @ManyToOne
    @JoinColumn(name = "category_id") // category_id 칼럼을 통해 Category 엔티티와 ManyToOne 관계로 연결
    private Category category;
    

    
    @PrePersist // 엔티티가 저장되기 직전에 실행
    protected void onCreate() {
        registeredAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate // 엔티티가 수정되기 직전에 실행
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
