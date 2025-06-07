package com.example.bookstore.dto;

import com.example.bookstore.entity.User;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    
    private Long id;
    
    @NotBlank(message = "아이디는 필수입니다")
    @Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하여야 합니다")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "아이디는 영문과 숫자만 사용 가능합니다")
    private String userId;
    
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다")
    private String password;
    
    @NotBlank(message = "이름은 필수입니다")
    @Size(max = 50, message = "이름은 50자를 초과할 수 없습니다")
    private String name;
    
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;
    
    @NotBlank(message = "연락처는 필수입니다")
    @Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$", message = "연락처는 000-0000-0000 형식이어야 합니다")
    private String phone;
    
    @NotBlank(message = "주소는 필수입니다")
    @Size(max = 200, message = "주소는 200자를 초과할 수 없습니다")
    private String address;
    
    private String status;
    private String grade;
    private LocalDateTime joinDate;
    private LocalDateTime lastLoginDate;
    private Set<String> roles;
    
    public static UserDto fromEntity(User user) {
        Set<String> roleStrings = null;
        if (user.getRoles() != null) {
            roleStrings = user.getRoles().stream()
                    .map(Enum::name)
                    .collect(Collectors.toSet());
        }
        
        return UserDto.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .status(user.getStatus() != null ? user.getStatus().name() : null)
                .grade(user.getGrade() != null ? user.getGrade().name() : null)
                .joinDate(user.getJoinDate())
                .lastLoginDate(user.getLastLoginDate())
                .roles(roleStrings)
                .build();
    }
    
    public User toEntity() {
        User user = new User();
        user.setId(this.id);
        user.setUserId(this.userId);
        user.setPassword(this.password);
        user.setName(this.name);
        user.setEmail(this.email);
        user.setPhone(this.phone);
        user.setAddress(this.address);
        user.setJoinDate(this.joinDate);
        user.setLastLoginDate(this.lastLoginDate);
        
        if (this.status != null) {
            try {
                user.setStatus(User.UserStatus.valueOf(this.status));
            } catch (IllegalArgumentException e) {
                // Default to ACTIVE if invalid
                user.setStatus(User.UserStatus.ACTIVE);
            }
        }
        
        if (this.grade != null) {
            try {
                user.setGrade(User.UserGrade.valueOf(this.grade));
            } catch (IllegalArgumentException e) {
                // Default to REGULAR if invalid
                user.setGrade(User.UserGrade.REGULAR);
            }
        }
        
        if (this.roles != null) {
            Set<User.UserRole> userRoles = new HashSet<>();
            for (String role : this.roles) {
                try {
                    userRoles.add(User.UserRole.valueOf(role));
                } catch (IllegalArgumentException e) {
                    // Skip invalid roles
                }
            }
            user.setRoles(userRoles);
        }
        
        return user;
    }
}
