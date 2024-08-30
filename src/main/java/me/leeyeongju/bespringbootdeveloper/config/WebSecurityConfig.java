package me.leeyeongju.bespringbootdeveloper.config;

import lombok.RequiredArgsConstructor;
import me.leeyeongju.bespringbootdeveloper.service.UserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    /*
    실제 인증 처리를 하는 시큐리티 설정 파일
     */

    private final UserDetailService userService;

    // 1. 스프링 시큐리티 기능 비활성화
    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers(toH2Console())
                .requestMatchers(new AntPathRequestMatcher("/static/**"));
    }

    // 2. 특정 HTTP 요청에 대한 웹 기반 보안 구성
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth // 3. 인증, 인가 설정
                        .requestMatchers("/login", "/signup", "/user").permitAll()
                        .anyRequest().authenticated())
                .formLogin(formLogin -> formLogin // 4. 폼 기반 로그인 설정
                        .loginPage("/login")
                        .defaultSuccessUrl("/articles", true)
                )
                .logout(logout -> logout // 5. 로그아웃 설정
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                )
                .csrf(csrf -> csrf.disable()) // 6. csrf 비활성화
                .build();
    }

    /*
    @Configuration : 스프링의 설정 클래스임을 나타낸다.
    @EnableWebSecurity : 웹 보안을 활성화 한다.
    @Bean : 스프링의 빈 정의 메서드임을 정의한다.

    1.
    @Bean
    public WebSecurityCustomizer configure() : `WebSecurityCustomizer`를 반환하여 특정 요청 경로를 스프링 시큐리티의 필터링에서 제외한다. => 즉 스프링 시큐리티의 모든 기능을 사용하지 않게 설정하는 코드이다.
    인증, 인가 서비스를 모든 곳에 적용하지 않는다. 일반적으로 정적 리소스(이미지, HTML 파일)에 설정을 한다.
    정적 리소스만 스프링 시큐리티 사용을 비활성화하는 데 `static` 하위 경로에 있는 리소스와 `h2`의 데이터를 확인하는 데 사용하는 `h2-console` 하위 `url`을 대상으로 `ignoring()` 메서드를 사용했다.

     */
}
