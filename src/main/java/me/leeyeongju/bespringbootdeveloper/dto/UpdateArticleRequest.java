package me.leeyeongju.bespringbootdeveloper.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor
@Getter
public class UpdateArticleRequest {
    /*
    블로그 글 수정 요청을 받을 DTO(data transfer object)
     */

    private String title;
    private String content;
}
