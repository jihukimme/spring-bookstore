package com.example.bookstore.repository;

import com.example.bookstore.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category save(Category category);
}
