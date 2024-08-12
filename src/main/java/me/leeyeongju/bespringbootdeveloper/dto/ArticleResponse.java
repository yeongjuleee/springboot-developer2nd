package me.leeyeongju.bespringbootdeveloper.dto;

import lombok.Getter;
import me.leeyeongju.bespringbootdeveloper.domain.Article;

@Getter
public class ArticleResponse {
    /*
    서비스에서 컨트롤러로 요청을 보낼 때, 응답을 보내는 DTO(data transfer object)

    ArticleResponse 는 서버에서 클라이언트에게 요청 받은 값(글의 제목과 내용)을 전달하는 값을 담은 객체이다.
     */

    private final String title;
    private final String content;

    public ArticleResponse(Article article) {
        // 글은 제목과 내용의 구성으로 엔티티를 인수로 받는 생성자를 추가.
        this.title = article.getTitle();
        this.content = article.getContent();
    }
}
