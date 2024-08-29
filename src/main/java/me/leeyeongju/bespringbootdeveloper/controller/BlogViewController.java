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
import org.springframework.web.bind.annotation.RequestParam;

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

    // 수정 화면을 보여주기 위한 newArticle() 메서드
    @GetMapping("/new-article")
    public String newArticle(@RequestParam(required = false) Long id, Model model) {
        // 1. id 키를 가진 쿼리 파라미터의 값을 id 변수에 매핑(id는 없을 수도 있다.)

        if(id == null) {
            // 2. id 파라미터가 가 없을 경우 : id 생성
            model.addAttribute("article", new ArticleViewResponse());
        } else {
            // 3. id 가 있을 경우 수정
            Article article = blogService.findById(id);
            model.addAttribute("article", new ArticleViewResponse(article));
        }

        return "newArticle"; // 4. newArticle.html 뷰 조회
    }

}
