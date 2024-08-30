package me.leeyeongju.bespringbootdeveloper.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Table(name ="users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Entity
public class User implements UserDetails {
    /*
    UserDetails 클래스를 상속하는 엔티티 클래스로 UserDetails를 상속받아 인증 객체로 사용한다.
     */
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;
    
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @Column(name = "password")
    private String password;
    
    @Builder
    public User(String email, String password, String auth) {
        this.email = email;
        this.password = password;
    }
    
    @Override // 권한 반환
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }
    
    // 사용자의 id를 반환(고유한 값)
    @Override
    public String getUsername() {
        return email;
    }
    
    // 사용자의 패스워드 반환
    @Override
    public String getPassword() {
        return password;
    }

    // 계정 상태 관련 메서드들(↓)

    // 계정 만료 여부 반환
    @Override
    public boolean isAccountNonExpired() {
        // 만료 되었는지 확인하는 로직
        return true; // true => 만료되지 않았다는 의미
    }
    
    // 계정 잠금 여부 반환
    @Override
    public boolean isAccountNonLocked() {
        // 계정 잠금되었는지 확인하는 로직
        return true; // true => 잠금되지 않았다는 의미
    }
    
    // 패스워드 만료 여부 반환
    @Override
    public boolean isCredentialsNonExpired() {
        // 패스워드가 만료되었는지 확인하는 로직
        return true; // true => 만료되지 않았다는 의미
    }
    
    // 계정 사용 가능 여부 반환
    @Override
    public boolean isEnabled() {
        // 계정이 사용 가능한지 확인하는 로직
        return true; // true => 사용 가능
    }

    /*
    @NoArgsConstructor(access = AccessLevel.PROTECTED) : 기본 생성자를 보호 수준으로 생성. 객체 생성 시 Builder 패턴을 사용하도록 유도한다.
    @GeneratedValue : ID가 자동으로 생성됨을 의미한다.

    public Collection<? extends GrantedAuthority> ... : UserDetails 인터페이스 메서드 구현한 부분으로 사용자의 권한을 반환한다. 해당 메서드에서는 `user` 라는 권한을 가진 `SimpleGrantedAuthority`를 반환하고 있으며, 여러 권한이 필요하다면 `List`에 추가할 수 있다.
     */
}
