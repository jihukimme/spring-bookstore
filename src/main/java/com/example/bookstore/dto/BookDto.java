package com.example.bookstore.dto;

import com.example.bookstore.entity.Book;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {
    
    private Long id;
    
    @NotBlank(message = "ISBN은 필수입니다")
    @Pattern(regexp = "^\\d{13}$", message = "ISBN은 13자리 숫자여야 합니다")
    private String isbn;
    
    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 200, message = "제목은 200자를 초과할 수 없습니다")
    private String title;
    
    @NotBlank(message = "저자는 필수입니다")
    @Size(max = 100, message = "저자는 100자를 초과할 수 없습니다")
    private String author;
    
    @NotBlank(message = "출판사는 필수입니다")
    @Size(max = 100, message = "출판사는 100자를 초과할 수 없습니다")
    private String publisher;
    
    @NotNull(message = "가격은 필수입니다")
    @DecimalMin(value = "0.0", inclusive = false, message = "가격은 0보다 커야 합니다")
    private BigDecimal price;
    
    private String imageUrl;
    private String pdfPreviewUrl;
    
    @Size(max = 1000, message = "설명은 1000자를 초과할 수 없습니다")
    private String description;
    
    private String size;
    
    @DecimalMin(value = "0.0", message = "평점은 0 이상이어야 합니다")
    @DecimalMax(value = "5.0", message = "평점은 5 이하여야 합니다")
    private Double rating;
    
    @Min(value = 0, message = "판매지수는 0 이상이어야 합니다")
    private Integer salesIndex;
    
    @Min(value = 0, message = "재고수량은 0 이상이어야 합니다")
    private Integer stockQuantity;
    
    private String status;
    private LocalDateTime registeredAt;
    private LocalDateTime updatedAt;
    private Long categoryId;
    
    public static BookDto fromEntity(Book book) {
        return BookDto.builder()
                .id(book.getId())
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .price(book.getPrice())
                .imageUrl(book.getImageUrl())
                .pdfPreviewUrl(book.getPdfPreviewUrl())
                .description(book.getDescription())
                .size(book.getSize())
                .rating(book.getRating())
                .salesIndex(book.getSalesIndex())
                .stockQuantity(book.getStockQuantity())
                .status(book.getStatus() != null ? book.getStatus().name() : null)
                .registeredAt(book.getRegisteredAt())
                .updatedAt(book.getUpdatedAt())
                .categoryId(book.getCategory() != null ? book.getCategory().getId() : null)
                .build();
    }
    
    public Book toEntity() {
        Book book = Book.builder()
                .id(this.id)
                .isbn(this.isbn)
                .title(this.title)
                .author(this.author)
                .publisher(this.publisher)
                .price(this.price)
                .imageUrl(this.imageUrl)
                .pdfPreviewUrl(this.pdfPreviewUrl)
                .description(this.description)
                .size(this.size)
                .rating(this.rating)
                .salesIndex(this.salesIndex)
                .stockQuantity(this.stockQuantity)
                .registeredAt(this.registeredAt)
                .updatedAt(this.updatedAt)
                .build();
        
        if (this.status != null) {
            try {
                book.setStatus(Book.BookStatus.valueOf(this.status));
            } catch (IllegalArgumentException e) {
                // Default to SELLING if invalid
                book.setStatus(Book.BookStatus.SELLING);
            }
        }
        
        return book;
    }
}
