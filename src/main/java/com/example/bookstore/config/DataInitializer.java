package com.example.bookstore.config;

import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Category;
import com.example.bookstore.enums.BookStatus;
import com.example.bookstore.enums.CategoryLevel;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.CategoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;

    @PostConstruct
    public void init() {
        // 이미 데이터가 있으면 실행하지 않음
        if (categoryRepository.count() > 0) {
            System.out.println("[DataInitializer] 데이터가 이미 존재하여 초기화를 생략합니다.");
            return;
        }

        // ===== 카테고리 생성 =====
        Category literature = categoryRepository.save(new Category(null, "문학", null, new ArrayList<>(), CategoryLevel.LARGE));
        Category humanities = categoryRepository.save(new Category(null, "인문학", null, new ArrayList<>(), CategoryLevel.LARGE));

        Category novel = categoryRepository.save(new Category(null, "소설", literature, new ArrayList<>(), CategoryLevel.MEDIUM));
        Category poem = categoryRepository.save(new Category(null, "시집", literature, new ArrayList<>(), CategoryLevel.MEDIUM));
        Category philosophy = categoryRepository.save(new Category(null, "철학", humanities, new ArrayList<>(), CategoryLevel.MEDIUM));
        Category history = categoryRepository.save(new Category(null, "역사", humanities, new ArrayList<>(), CategoryLevel.MEDIUM));

        Category sf = categoryRepository.save(new Category(null, "SF소설", novel, new ArrayList<>(), CategoryLevel.SMALL));
        Category romance = categoryRepository.save(new Category(null, "로맨스소설", novel, new ArrayList<>(), CategoryLevel.SMALL));
        Category ancient = categoryRepository.save(new Category(null, "고대사", history, new ArrayList<>(), CategoryLevel.SMALL));
        Category modern = categoryRepository.save(new Category(null, "근현대사", history, new ArrayList<>(), CategoryLevel.SMALL));

        // ===== 책 생성 =====
        List<Book> books = List.of(
                createBook("시간을 달리는 소녀", "츠츠이 야스타카", "지학사", 15000.0, 4.5, 200, 50, BookStatus.SELLING, sf),
                createBook("화성 연대기", "레이 브래드버리", "황금가지", 18000.0, 4.3, 130, 40, BookStatus.SELLING, sf),
                createBook("러브레터", "이와이 순지", "북스톤", 14000.0, 4.6, 250, 70, BookStatus.SELLING, romance),
                createBook("그 해 우리는", "정지우", "달빛", 13500.0, 4.1, 120, 30, BookStatus.SELLING, romance),
                createBook("달빛 아래 시", "윤동주", "문학사", 12000.0, 4.8, 310, 80, BookStatus.SELLING, poem),
                createBook("사랑이 없다면", "나태주", "푸른책들", 11000.0, 4.7, 290, 60, BookStatus.SELLING, poem),
                createBook("니체의 말", "프리드리히 니체", "인간사랑", 16000.0, 4.4, 140, 45, BookStatus.SELLING, philosophy),
                createBook("존재와 시간", "하이데거", "열린책들", 19000.0, 4.2, 80, 25, BookStatus.SELLING, philosophy),
                createBook("삼국사기", "김부식", "고전연구사", 17000.0, 4.5, 90, 35, BookStatus.SELLING, ancient),
                createBook("고구려", "김진명", "이야기출판사", 16500.0, 4.3, 100, 20, BookStatus.SELLING, ancient),
                createBook("근현대사 이야기", "한홍구", "역사비평사", 18000.0, 4.6, 200, 50, BookStatus.SELLING, modern),
                createBook("1945", "박태균", "역사출판", 15000.0, 4.4, 180, 60, BookStatus.SELLING, modern),
                createBook("소설가의 일", "무라카미 하루키", "문학사", 17500.0, 4.3, 210, 70, BookStatus.SELLING, novel),
                createBook("백년의 고독", "가브리엘 마르케스", "민음사", 20000.0, 4.7, 300, 80, BookStatus.SELLING, novel),
                createBook("인간 실격", "다자이 오사무", "더스토리", 13000.0, 4.6, 240, 55, BookStatus.SELLING, novel)
        );

        bookRepository.saveAll(books);
    }

    private Book createBook(String title, String author, String publisher, Double price,
                            Double rating, int salesQty, int stockQty, BookStatus status, Category category) {
        return Book.builder()
                .isbn("ISBN-" + System.nanoTime())
                .title(title)
                .author(author)
                .publisher(publisher)
                .price(price)
                .imageUrl("https://picsum.photos/seed/" + title.hashCode() + "/200/300")
                .description("이 책은 " + title + "에 대한 설명입니다.")
                .rating(rating)
                .salesQuantity(salesQty)
                .stockQuantity(stockQty)
                .status(status)
                .registeredAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .category(category)
                .build();
    }
}
