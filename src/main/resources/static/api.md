## 블로그 글 작성을 위한 API 구현 
* 서비스 클래스에서 메서드를 구현 -> 컨트롤러에서 사용할 메서드 구현 -> API 테스트 
  * 클라이언트(3. 테스트(POSTMAN), 4. 테스트 코드 작성(BlogControllerTest)) <--요청/응답(POST /api/articles)--> 2. 컨트롤러(BlogController) <--save()--> 1. 서비스(BlogService) ---> 리포지터리(BlogRepository)

* 실제 데이터를 확인하기 위한 API 실행 테스트 하기 
  1. resources 폴더의 application.yml 파일 편집
    ```
    spring:
      datasource:
        url: jdbc:h2:mem:testdb
      h2:
        console:
          enabled: true
    ```
  2. 스프링부트 서버 실행
  3. POSTMAN 실행, HTTP 메서드를 [POST]로 설정, URL에 http://localhost:8080/api/articles, [Body]를 [raw -> JSON] 변경
  ```
  {
    "title": "제목",
    "content":"내용"
  }
  ```
  작성 후 [Send] 를 눌러 요청 보내고, 아래 [Body]에서 Pretty 모드로 결과 출력 
  => 실제 값이 스프링 부트 서버 저장됨. 이 과정을 HTTP 메서드 POST로 서버에 요청해 값을 저장하는 과정

---
## 블로그 글 목록 조회를 위한 API 구현
클라이언트는 DB에 직접 접근할 수 없다. 때문에 API를 구현해볼 수 있도록 해야한다. 
블로그의 모든 글을 조회하는 API, 글 내용을 조회하는 API

* 서비스 메서드 코드 작성 
  1. BlogService 클래스 파일을 열어 DB에 저장되어 있는 모든 글을 가져오는 `findAll()` 메서드 추가
  2. 요청을 받아 서비스에 전달하는 컨트롤러 생성하는데, 먼저 응답을 위한 DTO를 작성한다. 
  ```
  // dto 패키지에 ArticleResponse 생성,
  
  @Getter
  public class ArticleResponse {
    
    private final String title;
    private final String content;
  
    public ArticleResponse(Article article) {
      this.title = article.getTitle();
      this.content = article.getContent();
    }
  
  } 
  ```
  3. controller 패키지의 BlogApiController 파일을 열어 전체 글을 조회한 뒤 반환하는 findAllArticles() 메서드 추가
---
## 블로그 글 조회 API 구현하기 
블로그 글 하나를 조회하는 API이다. 

* 1단계 : 서비스 메서드 코드 작성하기 
  1. BlogService 파일을 열어 블로그 글 하나를 조회하는 메서드 `findById()` 메서드 추가
  ```
  public Article findById(long id) {
    return blogRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
  }
  ```
* 2단계 : 컨트롤러 메서드 코드 작성하기 
  1. `/api/articles/{id}` GET 요청이 오면 블로그 글을 조회하기 위해 매핑할 `findArticle()` 메서드 작성
  ```
      @GetMapping("/api/articles/{id}") // URL 경로에서 값을 추출한다. {id}에 해당하는 값이 id로 들어온다.
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable long id ) { // URL에서 {id} 값이 id로 들어온다.
        Article article = blogService.findById(id);

        return ResponseEntity.ok()
                .body(new ArticleResponse(article));
    }
  ```
  2. 테스트 코드 작성 : Given : 블로그 글 저장 / When : 저장한 블로그 글의 id 값으로 API 호출 / Then : 응답 코드 200 OK이고, 반환받은 content 와 title이 저장된 값과 같은지 확인 과정 
---
## 블로그 글 삭제 API 구현하기
ID에 해당하는 블로그 글을 삭제하는 API 
* 1단계 : 서비스 메서드 코드 작성하기
  1. BlogService 파일을 열어 JPA에서 제공하는 `deleteById()`를 이용하여 데이터베이스에서 데이터를 삭제하는 `delete()` 메서드 추가 
  ```
  ```
* 2단계 : 컨트롤러 메서드 코드 작성하기 
  1. `/api/articles/{id}` 의 `DELETE` 요청이 오면 글을 삭제하기 위한 `findArticles()` 메서드 작성 
  ```
  @DeleteMapping("/api/articles/{id}") 
  public ResponseEntity<Void> deleteArticle(@PathVariable long id) {
        blogService.delete(id);
        return ResponseEntity.ok()
                .build();
  }
  ```
  2. 실행 테스트 하기 : `PostMan` 을 실행하고, `DELETE`로 HTTP 메서드를 설정, URL에 http://localhost:8080/api/articles/1 입력
     * 만약 `status: 405` 가 나온다면, 서버를 재 실행 후 `resources/schema.sql` 를 실행하고 `data.sql`을 실행한다.
     * `PostMan` 에서 `GET` 으로 HTTP 메서드를 설정하고, URL에 http://localhost:8080/api/aritcles 를 입력하고 send 해서 데이터가 있는지 확인을 한다. 
     * 데이터가 존재하면 다시 `DELETE`로 HTTP 설정 후 제거 확인

* 3단계 : 테스트 코드 작성 
---
## 블로그 글 수정 API 구현하기
특정 아이디의 글을 수정하는 API 
* 1단계 : `Article` 파일을 열어 수정하는 메서드 작성 
  1. 엔티티에 요청받은 내용으로 값을 수정하는 `update()` 메서드 작성 
  ```
  public void update(String title, String content) {
        this.title = title;
        this.content = content;
  }
  ```
* 2단계 : 블로그 글 수정 요청을 받을 DTO 작성 
  1. dto 패키지의 `UpdateArticleRequest` 생성
  ```
  @NoArgsConstructor @AllArgsConstructor
  @Getter
  public class UpdateArticleRequest {

    private String title;
    private String content;
  }
  ```
* 3단계 : 서비스 메서드 코드 작성하기 
  1. `BlogService` 에 글을 수정하는 `update()` 메서드 추가 
  ```
  @Transactional
  public Article update(long id, UpdateArticleRequest request) {
      Article article = blogRepository.findById(id)
              .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
      article.update(request.getTitle(), request.getContent());
  
      return article;
  }
  ```
* 4단계 : 컨트롤러 메서드 코드 작성하기 
  1. `/api/articles/{id}` PUT 요청이 오면 글을 수정하기 위한 `updateArticle()` 메서드 작성
  ```
  @PutMapping("/api/articles/{id}")
  public ResponseEntity<Article> updateArticle(@PathVariable long id, @RequestBody UpdateArticleRequest request) {
    Article updateArticle = blogService.update(id, request);
  
      return ResponseEntity.ok()
              .body(updateArticle);
    }
  ```
* 5단계 : 실행하기
  1. 포스트맨에서 HTTP 메서드를 `PUT` 으로 설정, URL은 http://localhost:8080/api/articles/1 입력 
  2. 수정 내용을 입력하기 위해 `Body` 탭 클릭, `row` 클릭, `JSON`으로 설정 
  3. "title" : ..., "content" : ... 부분을 수정하고 `send` 로 값 전송
  4. HTTP 메서드를 `Get` 으로 설정, URL은 http://localhost:8080/api/aritlcles 입력 => 바뀐 값 확인하기 
* 6단계 : 테스트 코드 작성하기 
---
## 블로그 글 목록 뷰 구현하기 
1. 컨트롤러 메서드 작성하기
   * 컨트롤러 메서드 작성하기 : 뷰에게 데이터를 전달하기 위한 객체 생성 
   ```
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
   ```
   * `/articles` GET 요청을 처리할 코드 작성하기 
   ```
    @RequiredArgsConstructor 
    @Controller
    public class BlogViewController {

    private final BlogService blogService;

    @GetMapping("/articles")
    public String getArticles(Model model) {
            List<ArticleListViewResponse> articles = blogService.findAll().stream()
                .map(ArticleListViewResponse::new)
                .toList();

        model.addAttribute("articles", articles); // 1. 블로그 글 리스트 저장

        return "articleList"; // 2. articleList.html 뷰 조회 
    }
    }
   ```
2. `HTML` 뷰 만들고 테스트하기 : 모델에 전달한 블로그 글 리스트 개수만큼 반복해 글 정보를 보여주는 코드 작성
   * 아무내용
   ```
   ```
   * 아무내용
3. 아무내용 