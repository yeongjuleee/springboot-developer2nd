package me.leeyeongju.bespringbootdeveloper.controller;

/*
URL에 매핑하기 위한 컨트롤러 메서드 추가로, @GetMapping, @PostMapping, @PutMapping, @DeleteMapping 등을 사용할 수 있다.
즉, HTTP 메서드에 대응한다.
 */

import lombok.RequiredArgsConstructor;
import me.leeyeongju.bespringbootdeveloper.domain.Article;
import me.leeyeongju.bespringbootdeveloper.dto.AddArticleRequest;
import me.leeyeongju.bespringbootdeveloper.dto.ArticleResponse;
import me.leeyeongju.bespringbootdeveloper.service.BlogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController // HTTP Response Body에 객체 데이터를 JSON 형식으로 반환하는 컨트롤러
public class BlogApiController {

    private final BlogService blogService;

    // HTTP 메서드에 POST일 때 전달받은 URL과 동일하면 메서드로 매핑한다.
    @PostMapping("/api/articles")
    // @RequestBody로 요청 본문 값 매핑
    public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest request) {
        Article savedArticle = blogService.save(request);
        
        // 요청한 자원이 성공적으로 생성되면 저장된 블로그 글 정보를 응답 객체에 담아 전송한다.
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedArticle);
    }

    // 전체 글을 조회한 뒤 반환하는 findAllArticles() 메서드 : BlogService 의 findAll() 메서드의 요청을 받아 실행된다.
    @GetMapping("/api/articles")
    public ResponseEntity<List<ArticleResponse>> findAllArticles() {
        // ResponseEntity, 전송을 하는 엔티티 타입의 객체를 반환하는 것으로 이것은 HTTP 응답을 나타낸다. 여기서 HTTP 응답을 하는 타입은 List<ArticleResponse> 타입이다.

        List<ArticleResponse> articles = blogService.findAll()
                .stream() // stream : 여러 데이터가 모여 있는 컬렉션을 간편하게 처리하는 기능
                .map(ArticleResponse::new)
                .toList();

        return ResponseEntity.ok()
                .body(articles);
    }

    // 글 하나만 조회하는 findArticle() 메서드
    @GetMapping("/api/articles/{id}") // URL 경로에서 값을 추출한다. {id}에 해당하는 값이 id로 들어온다.
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable long id ) { // URL에서 {id} 값이 id로 들어온다.
        Article article = blogService.findById(id);

        return ResponseEntity.ok()
                .body(new ArticleResponse(article));
    }

    // 글을 삭제하기 위한 findArticles() 메서드
    @DeleteMapping("/api/articles/{id}") // /api/articles/{id} DELETE 요청이 오면 {id}에 해당하는 값이 @PathVariable 어노테이션을 통해 들어옴
    public ResponseEntity<Void> deleteArticle(@PathVariable long id) {
        blogService.delete(id);

        return ResponseEntity.ok()
                .build();
    }

    /*
    @RestController : HTTP 응답으로 객체 데이터를 JSON 형식으로 반환
    @PostMapping() : HTTP 메서드가 POST일 때 요청받은 URL과 동일한 메서드 매핑(BlogApiController의 경우 /api/articles는 addArticle() 메서드에 매핑을 함.
    @RequestBody : HTTP를 요청할 때 응답에 해당하는 값을 @RequestBody 어노테이션이 붙은 대상 객체 AddArticleRequest에 매핑한다. ResponseEntity.status().body()는 응답 코드로 201, Created 를 응답하고 테이블에 저장된 객체를 반환한다. 
    
        200 Ok : 요청이 성공적으로 수행 됨
        201 Created : 요청이 성공적으로 수행되었고, 새로운 리소스가 생성됨
        400 Bad Request : 요청 값이 잘못 되어 요청 실패
        403 Forbidden : 권한이 없어 요청에 실패
        404 Not Found : 요청 값으로 찾은 리소스가 없어 요청 실패
        500 Internal Server Error : 서버 상에 문제가 있어 요청 실패

    H2 콘솔에 접속해 쿼리를 입력하고 데이터가 저장되는지 확인하는 반복 작업을 줄여 줄 테스트 코드 작성하기
    AlogApiController 클래스에 Alt + Enter 를 누르고 [Create Test] 클릭하면 테스트 코드 파일을 생성할 수 있음.
    이것을 이용하여 테스트 코드를 작성!

    findAllArticles() 메서드 :
        ~ .stream() : List<Article> 객체를 Stream<Article> 객체로 변환한다. Stream은 JAVA8에서 도입된 기능으로 컬렉션을 효율적으로 처리할 수 있도록 도와준다.

        ~ .map(ArticleResponse::new) : Stream<Article>을 Stream<ArticleResponse>로 변환한다. Article 객체를 ArticleResponse 객체로 변환하기 위해 ArticleResponse 생성자를 사용한다. AticleResponse::new : ArticleResponse 클래스의 생성자를 참조하여 Article 객체를 ArticleResponse 객체로 변환한다.
        ~ .toList() : 변환된 Stream<ArticleResponse> 객체를 다시 List<ArticleResponse>로 변환한다.

    findArticle() 메서드 :
        @PathVariable : URL에서 값을 가져오는 어노테이션이다.
        /api/articles/3 GET 요청을 받으면 id에 3 값이 들어오고, 이 값은 blogService의 findById() 메서드로 넘어가 3번 블로그 글을 찾는다.
        블로그 글을 찾으면 3번 글의 정보를 body에 담아 웹 브라우저로 전송한다.

     */
}
