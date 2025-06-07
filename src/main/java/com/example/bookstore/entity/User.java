package com.example.bookstore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String userId;
    
    private String password;
    private String name;
    private String email;
    private String phone;
    private String address;
    
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    
    @Enumerated(EnumType.STRING)
    private UserGrade grade;
    
    private LocalDateTime joinDate;
    private LocalDateTime lastLoginDate;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roles = new HashSet<>();
    
    public enum UserStatus {
        ACTIVE("활성"),
        INACTIVE("비활성"),
        DORMANT("휴면"),
        WITHDRAWN("탈퇴");
        
        private final String displayName;
        
        UserStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum UserGrade {
        REGULAR("일반"),
        SILVER("실버"),
        GOLD("골드"),
        VIP("VIP");
        
        private final String displayName;
        
        UserGrade(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum UserRole {
        ROLE_USER, ROLE_ADMIN
    }
    
    @PrePersist
    protected void onCreate() {
        joinDate = LocalDateTime.now();
        lastLoginDate = LocalDateTime.now();
        if (status == null) {
            status = UserStatus.ACTIVE;
        }
        if (grade == null) {
            grade = UserGrade.REGULAR;
        }
    }
}
