package me.leeyeongju.bespringbootdeveloper.config;

import lombok.RequiredArgsConstructor;
import me.leeyeongju.bespringbootdeveloper.service.UserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
                .authorizeHttpRequests(auth -> auth // 1. 인증, 인가 설정
                        .requestMatchers("/login", "/signup", "/user").permitAll()
                        .anyRequest().authenticated())
                .formLogin(formLogin -> formLogin // 2. 폼 기반 로그인 설정
                        .loginPage("/login")
                        .defaultSuccessUrl("/articles", true)
                )
                .logout(logout -> logout // 3. 로그아웃 설정
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                )
                .csrf(csrf -> csrf.disable()) // 4. csrf 비활성화
                .build();
    }

    // 3. 인증 관리자 관련 설정
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailService userDetailService) throws Exception {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService); // 1. 사용자 정보 서비스 설정
        authProvider.setPasswordEncoder(bCryptPasswordEncoder);

        return new ProviderManager(authProvider);
    }

    // 4. 패스워드 인코더로 사용할 빈 등록
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
    @Configuration : 스프링의 설정 클래스임을 나타낸다.
    @EnableWebSecurity : 웹 보안을 활성화 한다.
    @Bean : 스프링의 빈 정의 메서드임을 정의한다.

    해당 설정 코드들은 람다식(Lamda) 표현식을 사용하여 작성 되었다.
    1. 스프링 시큐리티 비활성화 설정
    @Bean
    public WebSecurityCustomizer configure() : `WebSecurityCustomizer`를 반환하여 특정 요청 경로를 스프링 시큐리티의 필터링에서 제외한다. => 즉 스프링 시큐리티의 모든 기능을 사용하지 않게 설정하는 코드이다.
    인증, 인가 서비스를 모든 곳에 적용하지 않는다. 일반적으로 정적 리소스(이미지, HTML 파일)에 설정을 한다.
    정적 리소스만 스프링 시큐리티 사용을 비활성화하는 데 `static` 하위 경로에 있는 리소스와 `h2`의 데이터를 확인하는 데 사용하는 `h2-console` 하위 `url`을 대상으로 `ignoring()` 메서드를 사용했다.

    2. 특정 HTTP에 대한 보안 설정(최신 스프링 시큐리티 버전으로 `authorizeRequests`가 아닌 `authorizeHttpRequests`를 사용함)
    @Bean
    public SecurityFilterChain(HttpSecurity http) throws Exception() :
    특정 HTTP 요청에 대한 웹 기반 보안을 구성하는 역할을 하는 메서드로 스프링 시큐리티의 `SecurityFilterChain`을 설정하는 부분이다.
    각 요청에 대한 인증 및 인가를 설정하고, 로그인 및 로그아웃 기능을 처리하며 CSRF(Cross-Site Request Forgery) 보호 기능을 활성화하거나 비활성화 할 수 있다.

    3. 인증 관리자 관련 설정
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailService userDetailService) throws Exception() :
    사용자 정보를 가져올 서비스를 재정의하거나, 인증 방법(ex: LDAP, JDBC 기반 인증) 등을 설정할 때 사용한다.
    *** 이 코드를 사용하기 위해서는 BCryptPasswordEncoder 타입의 빈(Bean)을 자동 주입(autowire)할 수 없기 때문에 BCryptPasswordEncoder 를 빈으로 등록해야 한다. ***
    BCryptPasswordEncoder : 패스워드를 안전하게 해시하기 위한 빈을 등록한다.
    AuthenticationManager : 사용자 인증을 처리하는 매니저를 설정한다. 사용자 정보를 조회하고 패스워드를 검증하는데 사용된다.
    `authProvider.setUserDetailsService(userService); // 1. 사용자 정보 서비스 설정` : `userDetailsService()` 사용자 정보를 가져올 서비스를 설정하는 것으로 설정하는 서비스 클래스는 반드시 `USerDetailsService`를 상속받은 클래스여야 한다.
    `authProvider.setPasswordEncoder(bCryptPasswordEncoder)` : 에서 사용된 `passwordEncoder()`는 비밀번호를 암호화하기 위한 인코더 설정


    4. 패스워드 인코더로 사용할 빈 등록
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() :
    BCryptPasswordEncoder : 사용자의 패스워드를 안전하게 인코딩(해시)하기 위해 사용된다.
    해당 메서드는 `BCryptPasswordEncoder`를 스프링 컨텍승트의 빈(Bean)으로 등록하여 다른 빈들에서 주입받을 수 있도록 한다.
    패스워드 해시는 보안의 핵심 요소로, 데이터를 안전하게 보호한다. `BCrypt` 알고리즘은 강력한 보안을 제공하는 해시 함수이다.
     */
}
