package me.leeyeongju.bespringbootdeveloper.dto;

import lombok.Getter;
import me.leeyeongju.bespringbootdeveloper.domain.Article;

@Getter
public class ArticleListViewResponse {
    // View에 데이터를 전달하기 위한 객체

    private final Long id;
    private final String title;
    private final String content;

    public ArticleListViewResponse(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
    }
}
