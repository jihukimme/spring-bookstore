package com.example.bookstore.dto;

import com.example.bookstore.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    
    private Long id;
    private String name;
    private Long parentId;
    private String level;
    private List<CategoryDto> children = new ArrayList<>();
    
    public static CategoryDto fromEntity(Category category) {
        if (category == null) {
            return null;
        }
        
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .level(category.getLevel() != null ? category.getLevel().name() : null)
                .children(category.getChildren().stream()
                        .map(CategoryDto::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }
    
    public Category toEntity() {
        Category category = new Category();
        category.setId(this.id);
        category.setName(this.name);
        
        if (this.level != null) {
            try {
                category.setLevel(Category.CategoryLevel.valueOf(this.level));
            } catch (IllegalArgumentException e) {
                // Default to LARGE if invalid
                category.setLevel(Category.CategoryLevel.LARGE);
            }
        }
        
        return category;
    }
}
