package me.leeyeongju.bespringbootdeveloper.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.leeyeongju.bespringbootdeveloper.dto.AddUserRequest;
import me.leeyeongju.bespringbootdeveloper.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class UserApiController {
    /*
    회원 가입과 관련된 요청을 처리하는 컨트롤러
     */
    
    private final UserService userService;
    
    // 회원 가입 요청을 받으면 서비스 메서드를 사용해 사용자를 저장한 뒤, 로그인 페이지로 이동하는 메서드
    @PostMapping("/user")
    public String signup(AddUserRequest request) {
        userService.save(request); // 회원 가입 메서드 호출
        return "redirect:/login"; // 회원 가입이 완료된 이후에 로그인 페이지로 이동
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response,
                SecurityContextHolder.getContext().getAuthentication());

        return "redirect:/login";
    }
}
