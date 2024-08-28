package me.leeyeongju.bespringbootdeveloper.controller;

import lombok.RequiredArgsConstructor;
import me.leeyeongju.bespringbootdeveloper.domain.Article;
import me.leeyeongju.bespringbootdeveloper.dto.ArticleListViewResponse;
import me.leeyeongju.bespringbootdeveloper.dto.ArticleViewResponse;
import me.leeyeongju.bespringbootdeveloper.service.BlogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RequiredArgsConstructor // final 도 생성자를 자동으로 만들어주는 어노테이션
@Controller
public class BlogViewController {
    // /articles GET 요청을 처리할 코드로, 블로그 글 전체 리스트를 담은 뷰를 반환

    private final BlogService blogService;

    @GetMapping("/articles")
    public String getArticles(Model model) {
        List<ArticleListViewResponse> articles = blogService.findAll().stream()
                .map(ArticleListViewResponse::new)
                .toList();

        model.addAttribute("articles", articles); // 1. 블로그 글 리스트 저장

        return "articleList"; // 2. articleList.html 뷰 조회
    }
    
    // 블로그 글을 반환할 getArticle() 메서드 
    @GetMapping("/articles/{id}")
    public String getArticle(@PathVariable Long id, Model model) {
        Article article = blogService.findById(id); 
        model.addAttribute("article", new ArticleViewResponse(article)); // 1. 블로그 글을 저장
        
        return "article"; // 2. article.html 뷰 조회
    }
}
