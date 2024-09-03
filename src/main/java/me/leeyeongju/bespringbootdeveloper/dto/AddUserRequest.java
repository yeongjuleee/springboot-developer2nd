package me.leeyeongju.bespringbootdeveloper.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddUserRequest {
    /*
    사용자 정보를 담고 있는 객체
     */

    private String email;
    private String password;
}
