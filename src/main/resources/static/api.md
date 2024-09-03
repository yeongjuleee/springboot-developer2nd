## 블로그 글 작성을 위한 API 구현 
* 서비스 클래스에서 메서드를 구현 -> 컨트롤러에서 사용할 메서드 구현 -> API 테스트 
  * 클라이언트(3. 테스트(POSTMAN), 4. 테스트 코드 작성(BlogControllerTest)) <--요청/응답(POST /api/articles)--> 2. 컨트롤러(BlogController) <--save()--> 1. 서비스(BlogService) ---> 리포지터리(BlogRepository)

* 실제 데이터를 확인하기 위한 API 실행 테스트 하기 
  1. resources 폴더의 application.yml 파일 편집
    ```yml
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
  ```java
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
  ```java
  public Article findById(long id) {
    return blogRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
  }
  ```
* 2단계 : 컨트롤러 메서드 코드 작성하기 
  1. `/api/articles/{id}` GET 요청이 오면 블로그 글을 조회하기 위해 매핑할 `findArticle()` 메서드 작성
  ```java
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
  ```java
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
  ```java
  public void update(String title, String content) {
        this.title = title;
        this.content = content;
  }
  ```
* 2단계 : 블로그 글 수정 요청을 받을 DTO 작성 
  1. dto 패키지의 `UpdateArticleRequest` 생성
  ```java
  @NoArgsConstructor @AllArgsConstructor
  @Getter
  public class UpdateArticleRequest {

    private String title;
    private String content;
  }
  ```
* 3단계 : 서비스 메서드 코드 작성하기 
  1. `BlogService` 에 글을 수정하는 `update()` 메서드 추가 
  ```java
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
  ```java
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
   ```java
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
   ```java
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
   ```html
   <!DOCTYPE html>
    <html xmlns:th="http://www.thymeleaf.org" lang="ko">
    <head>
      <meta charset="UTF-8">
      <title>블로그 글 목록</title>
      <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css">
    
    </head>
    <body>
      <div class="p-5 mb-5 text-center</> bg-light">
        <h1 class="mb-3">My Blog</h1>
        <h4 class="mb-3">블로그에 오신 것을 환영합니다.</h4>
      </div>
    
      <div class="container">
        <div class="row-6" th:each="item : ${articles}"> <!-- 1. article 개수만큼 반복 -->
          <div class="card">
            <div class="card-header" th:text="${item.id}">
            </div>
            <div class="card-body">
              <h5 class="card-title" th:text="${item.title}"></h5>
              <p class="card-text" th:text="${item.content}"></p>
              <a href="#" class="btn btn-primary">보러 가기</a>
            </div>
          </div>
          <br>
        </div>
      </div>
    </body>
    </html>
   ```
---
## 블로그 글 뷰 구현하기(글 상세보기)
1. 엔티티에 생성, 수정 시간 추가하기 
    * 엔티티에 생성 시간과 수정 시간 추가(`Article` 파일을 열어 필드 추가하기)
   ```java
    @CreatedDate // 엔티티가 생성될 때 생성 시간 저장
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate // 엔티티가 수정될 때 수정 시간 저장
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
   ```
   * 최초 파일 생성에도 값을 수정하도록 data.sql 파일을 수정하여 실행할 때마다 create_at, update_at이 바뀌도록 변경(1. `schema.sql`, 2. `data.sql`)
   ```sql
   CREATE TABLE IF NOT EXISTS article(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
    );
   ```
   ```sql
    INSERT INTO article (title, content, created_at, updated_at) values ('제목 1', '내용 1', NOW(), NOW());
    INSERT INTO article (title, content, created_at, updated_at) values ('제목 2', '내용 2', NOW(), NOW());
    INSERT INTO article (title, content, created_at, updated_at) values ('제목 3', '내용 3', NOW(), NOW())
   ```
   * `SpringBootDevleoperApplication` 파일을 열고 엔티티의 `create_at`, `updated_at`을 자동으로 업데이트 하기 위한 어노테이션 추가
   ```java
   @EnableJpaAuditing
   ```
2. 컨트롤러 메서드 작성하기
    * 뷰에서 사용할 DTO(data transfer object) 생성
    ```java
    @NoArgsConstructor @Getter
    public class ArticleViewResponse {


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
    ```
    * 블로그 글을 반환할 컨트롤러 메서드 작성 : `BlogViewController`에 아래 코드 추가
    ```java
    @GetMapping("/articles/{id}")
    public String getArticle(@PathVariable Long id, Model model) {
        Article article = blogService.findById(id); 
        model.addAttribute("article", new ArticleViewResponse(article)); // 1. 블로그 글을 저장
        
        return "article"; // 2. article.html 뷰 조회
    }
    ```
3. HTML 뷰 만들기
   * `resource/templates` 폴더에 `article` 생성
    ```html
    <!DOCTYPE html>
    <html xmlns:th="http://www.thymeleaf.org" lang="ko">
    <head>
      <meta charset="UTF-8">
      <title>블로그 글</title>
      <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css">
    </head>
    <body>
      <div class="p-5 mb-5 text-center</> bg-light">
        <h1 class="mb-3">My Blog</h1>
        <h4 class="mb-3">블로그에 오신 것을 환영합니다.</h4>
      </div>
    
      <div class="container mt-5">
        <div class="row">
          <div class="col-lg-8">
            <article>
              <header class="mb-4">
                <h1 class="fw-bolder mb-1" th:text="${article.title}"></h1>
                <div class="text-muted fst-italic mb-2" th:text="|Posted on ${#temporals.format(article.createdAt, 'yyyy-MM-dd HH:mm')}|"></div>
              </header>
    
              <section class="mb-5">
                <p class="fs-5 mb-4" th:text="${article.content}"></p>
              </section>
              <button type="button" class="btn btn-primary btn-sm">수정</button>
              <button type="button" class="btn btn-secondary btn-sm">삭제</button>
            </article>
          </div>
        </div>
      </div>
    </body>
    </html>
    ```
   * 글 상세 화면을 보러가기 위한 `articleList` 내용 수정 : 보러가기 버튼 수정하기(1. 기존 내용, 2. 수정된 내용)
    ```html
              <a href="#" class="btn btn-primary">보러 가기</a>
    ```
    ```html
              <a th:href="@{/articles/{id}(id=${item.id})}" class="btn btn-primary">보러 가기</a>
    ```
   * 실행하기

---
## 블로그 글 뷰 삭제 기능 
1. `src/main/resources/static` 폴더에 `js` 폴더 생성 후 `article.js`파일 생성 : 삭제 코드 작성
    ```javascript
    // 삭제 기능
    const deleteButton = document.getElementById('delete-btn');

    if (deleteButton) {
        deleteButton.addEventListener('click', () => {
            let id = document.getElementById('article-id').value;
            fetch(`/api/articles/${id}`, {
                method: 'DELETE'
        })
            .then( () => {
                alert('삭제가 완료되었습니다.');
                location.replace('/articles');
            });
        });
    }
    ```
2. 삭제 버튼을 눌렀을 때 삭제하도록 `삭제`버튼의 엘리먼트에 `delete-btn`이라는 아이디 값을 추가하고 `article.js`가 화면에서 동작하도록 임포트 한다.
3. 실행 테스트 하기 
---
## 글 수정, 생성 기능 추가하기
1. 수정/생성 뷰 컨트롤러 작성하기
   * 수정 화면을 보여주기 위한 컨트롤러 메서드 추가 : `BlogViewController`에 `newArticle()` 메서드 만들기
   ```java
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
    ```
2. 수정/생성 뷰 만들기
    * 컨트롤러 메서드에서 반환하는 `newArticle.html` 구현
    ```html
    <!DOCTYPE html>
    <html xmlns:th="http://www.thymeleaf.org" lang="ko">
    <head>
    <meta charset="UTF-8">
    <title>블로그 글</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css">
    </head>
    <body>
    <div class="p-5 mb-5 text-center</> bg-light">
    <h1 class="mb-3">My Blog</h1>
    <h4 class="mb-3">블로그에 오신 것을 환영합니다.</h4>
    </div>

    <div class="container mt-5">
    <div class="row">
      <div class="col-lg-8">
        <article>
          <!-- 아이디 정보 저장 -->
          <input type="hidden" id="article-id" th:value="${article.id}">

          <header class="mb-4">
            <input type="text" class="form-control" placeholder="제목" id="title" th:value="${article.title}">
          </header>

          <section class="mb-5">
            <textarea class="form-control h-25" rows="10" placeholder="내용" id="content" th:text="${article.content}"></textarea>
          </section>

          <!-- id가 있을 경우 [수정]버튼을, 없을 경우 [등록] 버튼이 보이도록 -->
          <button th:if="${article.id} != null" type="button" id="modify-btn" class="btn btn-primary btn-sm">수정</button>
          <button th:if="${article.id} == null" type="button" id="create-btn" class="btn btn-primary btn-sm">등록</button>
        </article>
      </div>
    </div>
    </div>

    <script src="/js/article.js"></script><!-- 수정, 생성을 위한 기능 API article.js -->
    </body>
    </html>
    ```
   * 수정, 생성 기능을 위한 API 구현 : `article.js` 에 수정 관련 기능 추가
    ```javascript
    // 수정 기능
    // 1. id가 modify-btn인 엘리먼트 조회
    const modifyButton = document.getElementById('modify-btn');

    if(modifyButton) {
    // 2. 클릭 이벤트가 감지되면 수정 API 요청
    modifyButton.addEventListener('click', () => {
    let params = new URLSearchParams(location.search);
    let id = params.get('id');

        fetch(`/api/articles/${id}`, {
            method: 'PUT',
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify( {
                title: document.getElementById('title').value,
                content: document.getElementById('content').value
            })
        })
            .then( () => {
                alert('수정이 완료되었습니다.');
                location.replace(`/articles/${id}`);
            });
    });
    }
    ```
   * `aritcle.html`에서 수정 버튼의 내용 수정(1. 기존 내용, 2. 수정된 코드) 
   ```html
              <button type="button" class="btn btn-primary btn-sm">수정</button>
    ```
    ```html
          <button type="button" id="modify-btn"
                  th:onclick="|location.href='@{/new-article?id={articleId}(articleId=${article.id})}'|"
                  class="btn btn-primary btn-sm">수정</button>
    ```
3. 실행 테스트하기 
---
## 생성 기능 마무리하기 
1. 생성 기능 작성하기
   * `resources/static/js`에서 `article.js`을 열어 등록 버튼을 누르면 입력 칸에 있는 데이터를 가져와 게시글 생성 API에 글 생성 관련 요청을 보내주는 코드 추가
    ```javascript
    // 생성(등록) 기능
    // 1. id가 create-btn인 엘리먼트
    const createButton = document.getElementById('create-btn');
    
    if(createButton) {
    // 2. 클릭 이벤트가 감지되면 생성 API 요청
    createButton.addEventListener("click", () => {
    fetch("/api/articles", {
    method: "POST",
    headers: {
    "Content-Type": "application/json",
    },
    body: JSON.stringify( {
    title: document.getElementById("title").value,
    content: document.getElementById("content").value,
    }),
    }). then( () => {
    alert("등록 완료되었습니다.");
    location.replace("/articles");
    });
    });
    }
    ```
   * `articleList.html` 파일을 수정하여 id가 `create-btn`인 [생성] 버튼 추가
    ```html
    
    ```
2. 실행 테스트하기

--- 
## 회원 도메인 만들기 
1. 의존성 추가하기
    ```
    // SECURITY
    implementation 'org.springframework.boot:spring-boot-starter-security' // 스프링 시큐링티를 사용하기 위한 스타터 
    implementation 'org.thymeleaf.extras:thymeleaf-extras-springsecurity6' // 스프링 시큐리티를 사용하기 위한 의존성 추가
    testImplementation 'org.springframework.security:spring-security-test' // 스프링 시큐리티를 테스트하기 위한 의존성
    ```
2. 엔티티 만들기 
   * `User` 파일 생성, `UserDetails` 클래스를 상속하는 `User` 클래스 만들기
    ```java
    import jakarta.persistence.*;
    import lombok.AccessLevel;
    import lombok.Builder;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import org.springframework.security.core.GrantedAuthority;
    import org.springframework.security.core.authority.SimpleGrantedAuthority;
    import org.springframework.security.core.userdetails.UserDetails;
    
    import java.util.Collection;
    import java.util.List;
    
    @Table(name ="users")
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter @Entity
    public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;
    
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @Column(name = "password")
    private String password;
    
    @Builder
    public User(String email, String password, String auth) {
        this.email = email;
        this.password = password;
    }
    
    @Override // 권한 반환
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }
    
    // 사용자의 id를 반환(고유한 값)
    @Override
    public String getUsername() {
        return email;
    }
    
    // 사용자의 패스워드 반환
    @Override
    public String getPassword() {
        return password;
    }

    // 계정 상태 관련 메서드들(↓)

    // 계정 만료 여부 반환
    @Override
    public boolean isAccountNonExpired() {
        // 만료 되었는지 확인하는 로직
        return true; // true => 만료되지 않았다는 의미
    }
    
    // 계정 잠금 여부 반환
    @Override
    public boolean isAccountNonLocked() {
        // 계정 잠금되었는지 확인하는 로직
        return true; // true => 잠금되지 않았다는 의미
    }
    
    // 패스워드 만료 여부 반환
    @Override
    public boolean isCredentialsNonExpired() {
        // 패스워드가 만료되었는지 확인하는 로직
        return true; // true => 만료되지 않았다는 의미
    }
    
    // 계정 사용 가능 여부 반환
    @Override
    public boolean isEnabled() {
        // 계정이 사용 가능한지 확인하는 로직
        return true; // true => 사용 가능
    }

    }
    ```
3. 리포지터리 만들기
   * `User` 엔티티에 대한 리포지터리 생성 : `repository` 폴더에 `UserRepository` 파일 생성, 인터페이스 설정
    ```java
    import java.util.Optional;
    
    public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // email로 사용자 정보를 가져온다.
    }
    ```
## 서비스 메서드 코드 작성하기
1. 로그인을 진행할 때 사용자 정보를 가져오는 코드 작성
    ```java
    import org.springframework.security.core.userdetails.UserDetailsService;
    import org.springframework.stereotype.Service;

    @RequiredArgsConstructor
    @Service
    public class UserDetailService implements UserDetailsService {

        private final UserRepository userRepository;

        // 사용자 이름(email)으로 사용자의 정보를 가져오는 메서드
        @Override
        public User loadUserByUsername(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow( () -> new IllegalArgumentException((email)));
        }
    }
    ```
---
## 시큐리티 설정하기
1. 실제 인증 처리를 하는 시큐리티 설정 파일 `WebSecurityConfig` 작성 : `config` 패키지 생성, `WebSecurityConfig` 클래스 작성 (최신 스프링 시큐리티에서 `authorizeRequests()`가 `authorizeHttpRequests()`로 대체 되었다. 1. 기존 `authorizeRequests()`, 2. 최신 `authorizeHttpRequests()`)
    ```java
    // 1. 기존 `authorizeRequests()`
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeRequests(auth -> auth // 3. 인증, 인가 설정
                        .requestMatchers(
                                new AntPathRequestMatcher("/login"),
                                new AntPathRequestMatcher("/signup"),
                                new AntPathRequestMatcher("/user")
                        ).permitAll()
                        .anyRequest().authenticated())
                .formLogin(formLogin -> formLogin // 4. 폼 기반 로그인 설정
                        .loginPage("/login")
                        .defaultSuccessUrl("/articles")
                )
                .logout(logout -> logout // 5. 로그아웃 설정
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)
                )
                .csrf(AbstractHttpConfigurer::disable) // 6. csrf 비활성화
                .build();
    }
    ```
---
위의 코드(기존 `authorizeRequests`) 설명 
2.
    ```
   @Bean
   public SecurityFilterChain filterChain(HttpSecurity http) throws Exception() :
   `SecurityFilterChain` 빈을 정의하여 `HTTP` 요청에 대한 보안 설정을 구성한다. => 즉 특정 HTTP 요청에 대해 웹 기반 보안을 구성한다.
    ```
3.
    ```
    return http .authorizeHttpRequests(auth -> auth) :
   특정 경로에 대한 액세스 설정을 한다.
   `requestMatchers()` : 특정 요청과 일치하는 url에 대한 액세스를 설정한다.
   `permitAll()` : 누구나 접근이 가능하게 설정한다. ("/login", "/signup", "/user"로 요청이 오면 인증/인가 없이도 접근할 수 있다.)
   `anyRequest()` : 위에서 설정한 url 이외 요청에 대해 설정한다.
   `authenticated()` : 별도의 인가는 필요하지 않지만 인증이 성공된 상태여야 접근할 수 있다.    
    ```
4.
    ```
   .formLogin(formLogin -> formLogin ... ) :
   `loginPage()` : 로그인 페이지 경로를 설정한다.
   `defaultSuccessUrl()` : 로그인이 완료되었을 때 이동할 경로 설정 
    ```
5.
    ```
   .logout(logout -> logout ...) :
   `logoutSuccessUrl()` : 로그아웃이 완료되었을 때 이동할 경로 설정
   `invalidateHttpSession()` : 로그아웃 이후에 세션을 전체 삭제할지 여부 설정
    ```
6.
    ```
   .csrf(AbstractHttpConfigurer::disable) :
   CSRF 설정을 비활성화 한다.
   ```
---
   ```java
   // 2. 최신 스프링 시큐리티 `authorizeHttpRequests()`
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
        .authorizeHttpRequests(auth -> auth // 3. 인증, 인가 설정
        .requestMatchers("/login", "/signup", "/user").permitAll()
        .anyRequest().authenticated())
        .formLogin(formLogin -> formLogin // 4. 폼 기반 로그인 설정
        .loginPage("/login")
        .defaultSuccessUrl("/articles", true)
        )
        .logout(logout -> logout // 5. 로그아웃 설정
        .logoutSuccessUrl("/login")
        .invalidateHttpSession(true)
        .clearAuthentication(true)
        )
        .csrf(csrf -> csrf.disable()) // 6. csrf 비활성화
        .build();
        }
   ```
---
위의 코드(최신 스프링 시큐리티 `authorizeHttpRequest`)에 대한 설명
1. `.authorizeHttpRequets(..)`
    ```
    .authorizeHttpRequests(auth -> auth
        .requestMatchers("/login", "/signup", "/user").permitAll()
        .anyRequest().authenticated())
   
   `authorizeHttpRequests` : 최신 버전에서 권장되는 메서드로 요청에 대한 보안을 더 간결하게 설정할 수 있다.
   `requestMatchers` : 문자열 경로를 직접 사용하여 경로를 매칭할 수 있다.
    ```
2. `.formLogin(...)`
    ```
    .formLogin(forLogin -> formLogin
        .loginPage("/login")
        .defaultSuccessUrl("/articles", true) 
   
   `defaultSuccessUrl` : 두번 째 인자(ture)는 `alwaysRedirect` 의미한다. true로 설정을 하면 항상 로그인 후에 리다이렉션 하도록 한다.
    ```
3. `logout(...)`
    ```
    .logout(logout -> logout
        .logoutSuccessUrl("/login)
        .invalidateHttpSession(true)
        .clearAuthentication(true)
   
   `clearAuthentication(true) : 로그아웃 시 인증 정보를 명확히 지우도록 설정한다.
    ```
4. `.crsf(...)`
    ```
    .csrf(csrf -> csrf.disable())
   `csrf` : 메서드의 인자로 `csrf -> csrf.disable()`를 사용하여 CSRF 보호를 비활성화 한다.
    ```
---
### 회원 가입 구현하기
1. 서비스 메서드 코드 작성
   * 사용자 정보를 담고 있는 객체 `AddUserRequest` DTO 작성
   ```
    @Getter
    @Setter
    public class AddUserRequest {
    
        private String email;
        private String password;
    }
   ```
   사용자로부터 입력받은 이메일과 패스워드 정보를 저장하는 DTO 클래스로 사용자의 요청이나 응답 데이터를 담는 용도로 사용되는데 해당 크래스는 유저 입장에서 서버로 데이터를 보내기 위한 목적. 

   * `AddUserRequest` 객체를 인수로 받는 회원 정보 추가 메서드 작성 : `UserService` 파일 생성
   ```
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
    
    }
   ```
   사용자 가입에 필요한 비즈니스 로직을 처리하는 서비스 클래스로 `AddUserRequest` DTO를 받아 사용자 정보를 저장하는 로직을 담당한다. DTO에서 전달받은 사용자 정보를 처리하고, 비즈니스 로직에 따라 데이터베이스에 안전하게 저장하는 역할을 한다.

2. 컨트롤러 작성
