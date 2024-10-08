package me.leeyeongju.bespringbootdeveloper.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserViewController {
    /*
    로그인, 회원 가입 경로로 접근하면 뷰 파일을 연결하는 컨트롤러
     */

    @GetMapping("/login")
    public String login() {
        return "login"; // `/login` 경로로 접근을 하면 `login()` 메서드가 `login.html`로 반환
    } 

    @GetMapping("/signup")
    public String signup() {
        return "signup"; // `/signup` 경로에 접근하면 `signup()` 메서드는 `signup.html` 으로 반환
    }
    
    /*
    SecurityContextLogoutHandler : 로그아웃 담당하는 핸들러
     */
}
