package com.example.bookstore.config;

import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.SearchTerm;
import com.example.bookstore.entity.User;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.SearchTermRepository;
import com.example.bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
// TODO: Spring Security 적용 시 주석 해제
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final SearchTermRepository searchTermRepository;
    // TODO: Spring Security 적용 시 주석 해제
    // private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Initialize admin user
        if (!userRepository.existsByUserId("admin")) {
            User admin = new User();
            admin.setUserId("admin");
            // TODO: Spring Security 적용 시 주석 해제하고 아래 라인 주석 처리
            // admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setPassword("admin123"); // 임시: 평문 비밀번호
            admin.setName("관리자");
            admin.setEmail("admin@bookstore.com");
            admin.setPhone("010-0000-0000");
            admin.setAddress("서울시 강남구");
            admin.setStatus(User.UserStatus.ACTIVE);
            admin.setGrade(User.UserGrade.VIP);
            admin.setRoles(new HashSet<>(Arrays.asList(User.UserRole.ROLE_ADMIN, User.UserRole.ROLE_USER)));
            userRepository.save(admin);
        }
        
        // Initialize test user
        if (!userRepository.existsByUserId("user")) {
            User user = new User();
            user.setUserId("user");
            user.setPassword("user123"); // 임시: 평문 비밀번호
            user.setName("테스트 사용자");
            user.setEmail("user@bookstore.com");
            user.setPhone("010-1234-5678");
            user.setAddress("서울시 서초구");
            user.setStatus(User.UserStatus.ACTIVE);
            user.setGrade(User.UserGrade.REGULAR);
            user.setRoles(new HashSet<>(Arrays.asList(User.UserRole.ROLE_USER)));
            userRepository.save(user);
        }
        
        // Initialize sample books
        if (bookRepository.count() == 0) {
            Book[] books = {
                Book.builder()
                    .isbn("9788936434267")
                    .title("모든 순간이 너였다")
                    .author("하태완")
                    .publisher("민음사")
                    .price(new BigDecimal("13500"))
                    .description("사랑과 이별, 그리고 다시 만남에 대한 이야기")
                    .rating(4.8)
                    .salesIndex(95)
                    .stockQuantity(150)
                    .status(Book.BookStatus.SELLING)
                    .registeredAt(LocalDateTime.now().minusDays(30))
                    .build(),
                    
                Book.builder()
                    .isbn("9788954682947")
                    .title("불편한 편의점")
                    .author("김호연")
                    .publisher("나무옆의자")
                    .price(new BigDecimal("12600"))
                    .description("편의점에서 벌어지는 따뜻한 이야기")
                    .rating(4.7)
                    .salesIndex(88)
                    .stockQuantity(0)
                    .status(Book.BookStatus.OUT_OF_STOCK)
                    .registeredAt(LocalDateTime.now().minusDays(25))
                    .build(),
                    
                Book.builder()
                    .isbn("9788936434274")
                    .title("달러구트 꿈 백화점")
                    .author("이미예")
                    .publisher("팩토리나인")
                    .price(new BigDecimal("12420"))
                    .description("꿈을 파는 신비한 백화점 이야기")
                    .rating(4.6)
                    .salesIndex(82)
                    .stockQuantity(75)
                    .status(Book.BookStatus.SELLING)
                    .registeredAt(LocalDateTime.now().minusDays(20))
                    .build(),
                    
                Book.builder()
                    .isbn("9788947545457")
                    .title("트렌드 코리아 2024")
                    .author("김난도")
                    .publisher("미래의창")
                    .price(new BigDecimal("17100"))
                    .description("2024년 대한민국 트렌드 전망")
                    .rating(4.5)
                    .salesIndex(79)
                    .stockQuantity(120)
                    .status(Book.BookStatus.SELLING)
                    .registeredAt(LocalDateTime.now().minusDays(15))
                    .build(),
                    
                Book.builder()
                    .isbn("9791168473690")
                    .title("세이노의 가르침")
                    .author("세이노")
                    .publisher("데이원")
                    .price(new BigDecimal("6480"))
                    .description("부와 성공에 대한 실용적 조언")
                    .rating(4.9)
                    .salesIndex(98)
                    .stockQuantity(200)
                    .status(Book.BookStatus.SELLING)
                    .registeredAt(LocalDateTime.now().minusDays(10))
                    .build()
            };
            
            bookRepository.saveAll(Arrays.asList(books));
        }
        
        // Initialize popular search terms
        if (searchTermRepository.count() == 0) {
            String[] terms = {
                "세이노의 가르침", "트렌드 코리아", "불편한 편의점", "달러구트", "모든 순간이 너였다",
                "원피스", "나루토", "해리포터", "자기계발", "소설"
            };
            
            for (int i = 0; i < terms.length; i++) {
                SearchTerm searchTerm = new SearchTerm();
                searchTerm.setTerm(terms[i]);
                searchTerm.setCount(100 - i * 5);
                searchTermRepository.save(searchTerm);
            }
        }
    }
}
