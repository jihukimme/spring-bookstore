package com.example.bookstore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.example.bookstore.enums.CategoryLevel;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name; // 카테고리 이름(문학, 인문학, SF소설)
    
    @ManyToOne // 카테고리(Many) -> 부모 카테고리(One)
    @JoinColumn(name = "parent_id") // DB에는 parent_id라는 FK 칼럼이 생기면서 참조함
    private Category parent; // 부모 카테고리
    
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL) // 부모 카테고리(One) -> 자식 카테고리(Many), Cascade = ALL : 부모가 삭제되면 자식도 함께 삭제
    private List<Category> children = new ArrayList<>();
    
    @Enumerated(EnumType.STRING)
    private CategoryLevel level; // LARGE일 때는 부모 카테고리를 가질 수 없게, SMALL일 때는 children을 가질 수 없도록 해야 함
}
