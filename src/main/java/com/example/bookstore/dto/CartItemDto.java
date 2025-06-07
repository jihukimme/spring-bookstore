package com.example.bookstore.dto;

import com.example.bookstore.entity.CartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    
    private Long id;
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookImageUrl;
    private BigDecimal bookPrice;
    private Integer quantity;
    private BigDecimal totalPrice;
    private boolean available;
    
    public static CartItemDto fromEntity(CartItem cartItem) {
        return CartItemDto.builder()
                .id(cartItem.getId())
                .bookId(cartItem.getBook().getId())
                .bookTitle(cartItem.getBook().getTitle())
                .bookAuthor(cartItem.getBook().getAuthor())
                .bookImageUrl(cartItem.getBook().getImageUrl())
                .bookPrice(cartItem.getBook().getPrice())
                .quantity(cartItem.getQuantity())
                .totalPrice(cartItem.getBook().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .available(cartItem.getBook().getStockQuantity() >= cartItem.getQuantity())
                .build();
    }
}
