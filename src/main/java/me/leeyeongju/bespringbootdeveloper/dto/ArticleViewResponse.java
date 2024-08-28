package me.leeyeongju.bespringbootdeveloper.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.leeyeongju.bespringbootdeveloper.domain.Article;

import java.time.LocalDateTime;

@NoArgsConstructor @Getter
public class ArticleViewResponse {
    /*
    View 에서 사용할 DTO
     */

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    public ArticleViewResponse(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.createdAt = article.getCreatedAt();
    }
}
