package me.leeyeongju.bespringbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import me.leeyeongju.bespringbootdeveloper.domain.User;
import me.leeyeongju.bespringbootdeveloper.dto.AddUserRequest;
import me.leeyeongju.bespringbootdeveloper.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    /*
    AddUserRequest 객체를 인수로 받는 회원 서비스 클래스
     */

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // AddUserRequest 객체를 인수로 받는 회원 정보 추가 메서드
    public Long save(AddUserRequest dto) {
        return userRepository.save(User.builder()
                .email(dto.getEmail())
                // 1. 패스워드 암호화
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .build()).getId();
    }
    
    /*
    1. AddUserRequest 객체를 인수로 받는 회원 정보 추가 메서드 
    save() : 
    패스워드를 저장할 때 시큐리티를 설정하며 패스워드 인코딩용으로 등록한 빈을 사용해서 암호화한 후 저장
    
     */
}
