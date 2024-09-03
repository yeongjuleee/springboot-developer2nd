# H2 DB에 값 넣기 설정

### 만약 서버를 실행할 때, DB에서 문제가 일어났다면 `application.yml` 에서 
```
ddl-auto: create 
## 위 부분을 확인해본다. 
```
Hibernate가 애플리케이션이 시작될 때 테이블을 생성하지만, 데이터가 삽입되기 전에 테이블이 준비되지 않는 문제가 발생할 수 있다. 특히, 
```
defer-datasource-initialization:ture
```
위 설정은 데이터 소스 초기화를 지연하겠다는 의미로 현재 `true` 값을 `false`로 변경하여 데이터 소스 초기화를 즉시 실행하도록 했다. 
`Hibernate`가 테이블을 생성하고 데이터 삽입이 바로 진행될 수 있다. 

### `schema.sql`과 `data.sql`파일 순서를 확인한다. 
`schema.sql`과 `data.sql` 파일이 올바른 순서로 실행되도록 한다.
`schema.sql` 파일은 테이블 생성에 사용되고, `data.sql`파일은 데이터 삽입에 사용된다. 
일반적으로 SpringBoot는 `schema.sql`을 먼저 실행하고 `data.sql`을 그 다음에 실행한다. 따라서 서버를 실행할 때 Database 관련 오류가 난다면, `schema.sql` 파일을 먼저 실행 후 서버를 실행하도록 한다. 


---
## BlogRepository의 역할과 인터페이스

### 인터페이스(Interface)
클래스가 구현해야 하는 메서드의 선언을 포함하는 일종의 계약서
인터페이스 자체는 메서드의 구현을 포함하지 않으며, 이를 구현하는 클래스가 메서드의 실제 내용을 정의해야 한다. 

```
 public interface BlogRepository extends JpaRepository<Article, Long> {
    // JpaRepository 가 기본적으로 제공하는 메서드들을 상속받아 사용
 }
```
`BlogRepository`는 JPA 리포지토리 인터페이스를 확장하는데, `JpaRepository<Article, Long>`는 JPA 리포지토리가 기본적으로 제공하는 CRUD(Create, Read, Update, Delete) 기능을 포함한다. 

### JpaRepository
Spring Data JPA에서 제공하는 인터페이스로, Entity 클래스(`Article`)와 그 식별자 타입(`Long`)을 매개변수로 받아 CRUD 및 페이징, 정렬 기능을 제공한다.

```
    public interface JpaRepository<T, ID> extends PagingAndSortingRepository<T, ID> {
        // 다양한 CRUD 메서드를 제공, T : Type, ID 는 데이터의 PK 값
    }
```

### BlogRepository 역할 
`BlogRepository`는 `JpaRepository`를 확장하여 `Article` 엔티티에 대한 기본적인 데이터 접근 메서드를 제공하는 인터페이스이다. 
Spring Data JPA가 인터페이스의 구현체를 자동으로 생성해준다. 
즉, 별도의 구현 없이도 DB와 상호작용을 할 수 있는 메서드를 사용할 수 있다.

### Service 에서 BlogRepository를 호출하는 이유
서비스 레이어는 비즈니스 로직을 담당하며 데이터 접근을 위해 리포지토리 레이어와 상호작용한다.
`BlogService`는 `BlogRepository`를 사용하여 데이터 베이스와 통신한다.

* BlogService 예시
```
    import lombok.RequiredArgsConstructor;
    import me.leeyeongju.bespringbootdeveloper.domain.Article;
    import me.leeyeongju.bespringbootdeveloper.dto.AddArticleRequest;
    import me.leeyeongju.bespringbootdeveloper.repository.BlogRepository;
    import org.springframework.stereotype.Service;

    @RequiredArgsConstructor
    @Service
    public class BlogService {

        private final BlogRepository blogRepository;

        // 블로그 글 추가 메서드
        public Article save(AddArticleRequest request) {
        return blogRepository.save(request.toEntity());
        }
    }

```
1. 의존성 주입 : `BlogService`는 `BlogRepository`를 의존성 주입을 받는다. 이것은 `BlogRepository`가 인터페이스이기 떄문에 Spring이 해당 인터페이스의 구현체를 제공해준다.
2. 데이터 저장 : `save` 메서드는 `AddArticleRequest` 객체를 받아 이것을 엔티티로 변환 후 `blogRepository.save` 메서드를 호출하여 데이터 베이스에 저장한다. 

### 결론,
 * `BlogRepository` : JPA 리포지토리를 확장한 인터페이스로 기본적 CRUD 작업을 위한 메서드를 제공
 * `interface` : 클래스가 구현해야 하는 메서드를 선언한 계약서이며, 직접 구현체를 가지지는 않음
 * `Service` 레이어 : 비즈니스 로직을 처리하며 `Repository` 레이어를 통해 DB와 상호작용한다. `BlogService`는 `BlogRepository`를 사용하여 DB에 접근한다.
 * `Spring Data JPA` : `BlogRepository` 인터페이스의 구현체를 자동으로 생성하여, 서비스 레이어가 데이터 접근 로직을 간단하게 처리할 수 있도록 도와준다. 

---

## Test 코드에서 사용되는 코드
### `Mock`과 `Mockmvc` 
1. `mock` : `Mock객체`는 실제 객체의 동작을 모방하기 위해 사용되는 객체이다. (물건을 판매하기 전에 가제품으로 미리 보는 것과 같음) 주로 단위 테스트에서 사용되며, 실제 객체가 없거나 테스트하기 어려울 때 사용한다. `Mock` 객체는 메모리 상에만 존재하고 외부 시스템(DB, 네트워크 등)과의 상호작용을 시뮬레이션한다.
   * 사용 예시 : 데이터베이스와 상호작용이 포함된 코드를 테스트할 때, 실제 데이터베이스를 사용하지 않고 `Mock` 객체를 사용해 동일한 메서드 호출을 시뮬레이션 할 수 있다. 
   * `Mock`을 사용해야하는 이유 :
     * 외부 시스템과의 의존성을 제거해서 테스트의 독립성을 보장한다.
     * 테스트를 더 빠르게 수행할 수 있다.
     * 다양한 시나리오(예외처리, 특정 조건)를 쉽게 테스트할 수 있다.
   * 라이브러리 : `Mockito` Java의 Mock 라이브러리로 객체의 동작을 시뮬레이션하고 테스트할 수 있도록 지원한다. 
2. `MockMvc` : Spring MVC의 웹 계층을 테스트하기 위해 사용되는 클래스이다. 실제 서버를 구동하지 않고도 HTTP 요청을 시뮬레이션하고, 컨트롤러의 동작을 테스트할 수 있다. 
   * 주요기능 :
     * HTTP 요청을 작성하고 테스트할 수 있도록 한다.
     * Spring MVC의 전체 웹 계층을 테스트할 수 있다.
     * 컨트롤러 메서드의 결과를 검증할 수 있다. 
     * 예시코드
     ```
        mockMvc.perform(get("/api/articles")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("title));
     ```
     `/api/articles` 엔드포인트에 대해 GET 요청을 시뮬레이션하고, 응답 상태 코드가 200 OK인지, 응답 JSON에서 `title` 필드가 "title"인지 확인한다.
   * Mockmvc 사용 이유:
     * 실제 서버를 띄우지 않고도 컨트롤러의 동작을 테스트할 수 있다.
     * 테스트를 간결하고 빠르게 수행할 수 있다.
     * 웹 계층의 전체 동작을 검증할 수 있다. 
3. `protected`와 `private` 선언의 이유 
    ```
   class BlogApiControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper; // 직렬화, 역직렬화를 위한 클래스

    @Autowired
    private WebApplicationContext context;
   }
    ```
* 위 코드에서 `protected` 선언된 `mockMvc`와 `objectMapper`
    * `protected` : 같은 클래스, 같은 패키지, 해당 클래스를 상속받은 자식 클래스에서 접근할 수 있다. 
    * 테스트 클래스에서는 `protected`를 사용하여 확장 가능한 테스트 구조를 만들 수 있다. 이 테스트 클래스를 상속받아 추가 테스트를 작성할 수 있고, 이런 경우 자식 클래스에서도 `mockMvc`와 `objectMapper`를 사용할 수 있다.
    * `mockMvc`와 `objectMapper`는 테스트에 자주 사용되는 객체들로 이를 상속된 클래스에서도 재사용할 수 있도록 `protected`로 선언한다. 
* `private`로 선언된 `WebApplicationContext` :
  * `private` : 선언된 클래스 내에서만 접근할 수 있다. 외부 클래스나 자식 클래스에서도 접근이 불가능하다. 
  * `WebApplicastionContext`는 Spring 애플리케이션의 모든 빈(Bean)을 관리하는 역할을 하며, 보통 내부적으로만 필요하기 때문에 `private` 접근 제한자를 사용한다.
* `WebApplicationContext` 역할 
  * Spring MVC에서 애플리케이션 컨텍스트를 확장하는 인터페이스이다. 웹 애플리케이션과 관련한 빈(Bean)을 고나리하며, Spring MVC의 설정과 생명주기를 관리한다.
    * Spring 애플리케이션 전체 설정을 포함하며, 각종 빈(Bean) 관리
    * MVC 설정 정보(Controller, View 등)를 포함한다.
    * 웹 애플리케이션이 구동될 때 초기화한다.
  * `MockMvc` 설정을 위해 `WebApplicationContext`를 사용하여 Spring 애플리케이션의 컨텍스트를 가져와 테스트 환경을 구성한다.

### `ResultActions`
`mockMvc.perform()` 메서드의 결과를 담는 객체이다. 테스트 결과에 대한 여러 가지 검증을 체이닝 방식으로 추가할 수 있도록 한다. 
* `mockMvc.perform()` : `MockMvc` 는 Spring MVC 애플리케이션의 컨트롤러 테스트를 사용할 때 사용하는 도구이다. `perform()` 메서드를 사용하여 HTTP 요청을 시뮬레이션한다.
    ```
    final ResultActions resultActions = mockMvc.perform(get(url, savedArticle.getId()));
    ```
  위 코드에서 `get(url, savedArticle.getId())` 의 `get`은 HTTP의 GET 요청을 의미한다. `url` 부분은 테스트 코드 초반부에 선언한 `private String url` 부분에서 정의한 주소이며, `savedArticle.getId()` 를 통하여 `url`의 `{id}` 부분에 저장된 글의 ID를 채워넣는다. 

### `jsonPath()`
`jsonPath` : JSON 응답에서 특정 경로의 값을 추출하는 데 사용된다. 
    ```
    jsonPath("$.content").value("content) 
    ```
    위 코드에서 `$.content`은 JSON 루트에서 `content` 필드를 말한다. `value(content)` 부분은 응답 JSON에서 추출된 `content` 필드의 값이 `content` 변수와 동일한지 검증을 한다. 

### `BlogServiceTest`와 `BlogApiControllerTest`의 차이 
1. `BlogService` : 애플리케이션의 핵심 비즈니스 로직을 처리하는 `서비스 레이어`이다. 
2. `BlogService`의 주요 역할: 
   * 비즈니스 로직 처리 : 클라이언트의 요청을 처리하고, 데이터를 가공하거나 검증 등의 작업을 한다.
   * 데이터베이스와 상호작용 : `Repository`와 상호작용하여 데이터를 저장, 조회, 수정, 삭제(CRUD)한다.
   * 컨트롤러와 연결 : 컨트롤러가 사용자로부터 받은 요청을 서비스 레이어에 전달하여 실제 작업을 수행한다. 
3. `BlogServiceTest` 작성 이유 :
   * 서비스 로직 검증 : `BlogService`에 구현된 비즈니스 로직이 예상대로 작동되는지 확인하기 위해 작성한다. 
   * 안정성 보장 : 서비스 레이어가 제대로 동작하는지 확인하여, 추후 코드 수정이나 기능 추가 시 기존 로직이 영향을 받지 않도록 한다.
   * 문제 조기 발견 : 코드에 문제가 있을 경우, 조기에 발견하여 수정할 수 있다. 
4. `BlogApiController` : 주로 HTTP 요청을 모의(Mock)하여 컨트롤러의 동작을 테스트한다.
5. `BlogServiceTest` : 서비스 레이어의 비즈니스 로직이 예상대로 동작하는지 검증한다. 

### `BlogServiceTest`에서 `MockMvc`와 `@AutoConfigureMockMvc`를 사용하지 않는 이유 
1. 테스트 계층 차이:
    * `BlogApiControllerTest` : 컨트롤러 계층을 테스트 하는 것으로 HTTP 요청을 모의하여 컨트롤러가 올바르게 동작하는지 검증하기 떄문에 `MockMvc`가 사용된다.  
    * `BlogServiceTest` : 서비스 계층을 테스트하는 것으로 서비스 계층은 HTTP 요청이나 웹 관련 기능을 다루지 않고, 비즈니스 로직과 데이터베이스 작업을 수행한다. 때문에 `MockMvc`는 필요없고 직접 `BlogService` 메서드를 호출하여 동작을 검증한다.
2. `MockMvc` 와 `@AutoConfigureMockMvc` : 
   * `MockMvc` : HTTP 요청을 모의(Mock)하여 컨트롤러를 테스트하기 위한 도구이다.
   * `@AutoConfigureMockMvc` : 스프링 테스트 컨텍스트에서 `MockMvc`를 자동으로 구성하는 어노테이션이다. 
   * 서비스 테스트느(`BlogServiceTest`)는 HTTP 레벨의 테스트가 아니라 로직 레벨의 테스트이므로 해당 도구들을 사용하지 않는다. 
### `isPresent()` 
`isPresnet()` : Java의 `Optional` 클래스에서 제공하는 메서드이다.
  * `Optional`은 값이 존재할 수도 있고, 존재하지 않을수도 있는 상황을 안전하게 처리하기 위해 도입된 클래스이다. 
  * `isPresnet()`는 `Optional` 객체가 값을 가지고 있으면 `true`를 반환, 값을 없으면 `false`를 반환한다. 

### `BlogServiceTest` 에서 사용된 메서드에 `public void`가 아니라 `void`가 사용된 이유 
`JUnit 5`에서는 테스트 메서드를 `public`으로 선언할 필요가 없다. `JUnit 4`까지는 테스트 메서드를 `public`으로 선언해야 했으나, `JUnit 5`에서는 접근 제한자에 대한 제약이 사라졌다. 
따라서 테스트 메서드는 기본 접근 제한자인 `package-private(접근 제한자를 명시하지 않은 상태)`를 사용할 수 있으며, 이것으로 `public void` 대신 단순히 `void`를 사용한다. 
---
### `assertThrows` 
테스트 시 예외 발생 여부를 명시적으로 테스트하기 위해 사용한다. 
* `BlogServieTest`에서 `findArticle()` 메서드에 `throws Exception`을 추가할 수 있으나, 해당 메서드에서 발생할 수 있는 예외는 런타임 예외`IllegalArgumentException`같은 예외만 가능하다. 
* 런타임 예외는 일반적으로 명시적으로 처리하지 않거나, 테스트 시 `assertThrows`와 같은 방식으로 처리한다. 

### `BlogServiceTest`에서 `findArticle()` 메서드에 바로 예외설정을 해놓고도 따로 `findArticleNotFound()` 테스트 메서드를 만드는 이유 
`BlogServiceTest`의 경우 `BlogApiControllerTest`와 달리 `MockMvc`와 같은 HTTP 요청을 모방하는 과정이 아닌 직접적으로 서비스 로직을 테스트하기 때문에 특정 예외가 발생하는지 확인하는 테스트를 작성하려면 `assertThrows`와 같은 메서드를 사용해야 한다. 
하지만, 테스트의 명확성을 위해서는 보통 시나리오를 개별 테스트 메서드로 분리하기 때문에 예외 테스트 코드도 작성하는 것이다.
---
### `@EnableJpaAuditing` 
스프링 데이터 JPA의 Auditing 기능을 활성화 하는 것으로 엔티티가 생성되거나 수정될 때 자동으로 시간 정보(생성 시간, 수정 시간)이나 사용자 정보(생성한 사람, 수정한 사람)를 기록할 수 있게 한다.
### `fetch()`
`JavaScript` 에서 `HTTP` 요청을 보내기 위해 사용되는 함수이다.
웹 애플리케이션이 서버와 비동기적으로 데이터를 주고받을 수 있게 한다. 

* `fetch()` 메서드의 기본 개념:
  * 역할 : 서버로부터 데이터를 가져오거나(`HTTP` GET 요청) 서버에 데이터를 전송하는(`HTTP` POST, PUT, DELETE 등 요청) 작업을 수행한다.
  * 비동기 처리 : 비동기 함수로, 요청이 완료될 때까지 페이지가 멈추지 않고 다른 작업을 계속할 수 있게 한다. 
  * Promise 기반 : `Promise` 객체를 반환한다. 이것은 요청이 성공하거나 실패할 때 이것을 처리할 수 있게 한다.
---
### `@RequestParam(required = false) Long id` 
URL의 쿼리 파라미터 `id`를 메서드의 매개 변수 `id`에 바인딩 한다. 
`required = false`로 설정되어 있어 `id`가 없어도 예외가 발생하지 않는다.

### `URLSearchParams` : `javaScript`
* `URLSearchParam` : 쿼리 문자열을 파싱하여 개별적인 키-값 쌍으로 접근할 수 있게 해주는 객체이다. 
* `let params = new URLSearchParams(location.search)` 의 경우 쿼리 문자열을 파싱하여 `params` 객체를 생성한다. 그 후 `params.get('id')`를 통해 `id`값을 추출한다.
* `location.search` : 현재 페이지의 URL에서 쿼리 문자열을 반환한다. URL이 `https://example.com/new-article?id=5` 라면, `location.search`는 `"?id=5"`를 반환한다.

---
### `UserDetails` 
스프링 시큐리티에서 인증(로그인)된 사용자의 정보를 관리하기 위해 `UserDetails` 인터페이스를 사용한다. 
`UserDetails` 인터페이스는 사용자 계정의 기본적인 정보를 제공하는 역할을 하며, `UserDetails`를 구현한 객체는 스프링 시큐리티가 인증한 권한 부여 작업을 수행하는 데 필요한 정보를 제공한다. 

### JPA 메서드에 맞게 식별자 관련 부분 : `UserRepository`
JPA는 메서드 규칙에 맞춰 메서드를 선언하면 이름을 분석해 자동으로 쿼리를 생성한다. 

* 자주 사용하는 쿼리 메서드의 규칙
  1. `findByName()` : "name" 컬럼의 값 중 파라미터로 들어오는 값과 같은 데이터 반환
    ```sql
    ...WHERE name = ?1
    ```
  2. `findByNameAndAge()` : 파라미터로 들어오는 값 중 첫 번째 값은 "name" 컬럼에서 조회하고, 두 번째 값은 "age" 컬럼에서 조회한 데이터 반환
    ```sql
    ...WHERE name =?1 AND age=?2
    ```
  3. `findByNameOrAge()` : 파라미터로 들어오는 값 중 첫 번째 값이 "name" 컬럼에서 조회되거나 두 번째 값이 "age"에서 조회되는 데이터 반환
    ```sql
    ...WHERE name=?1 OR age=?2
    ```
  4. `findByAgeLessThan()` : "age" 컬럼의 값 중 파라미터로 들어온 값보다 작은 데이터 반환
    ```sql
    ...WHERE age <?1
    ```
  5. `findByAgeGreaterThan()` : "age" 컬럼의 값 중 파라미터로 들어온 값보다 큰 데이터 반환
    ```sql
    ...WHERE age >?1
    ```
  6. `findByName(Is)Null()` : "name" 컬럼의 값 중 null인 데이터 반환
    ```sql
    ...WHERE name IS NULL
    ```
  
---
### `Optional<ClassName>` 
`Optional` 은 값이 없을 수도 있는 상황에서 `null`을 안전하게 처리하기 위한 컨테이너 역할을 한다. 
이 메서드가 반환하는 값이 존재하지 않으면 `Optional.empty()`가 반환이 된다.
---
### `WebSecurityConfig` 에서 `filterChain` 메서드의 `Lamda`표현식에 관하여
```java
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
1. `.formLogin`과 `.logout`의 빨간 밑줄이 생긴 것에 대하여 
    * 빨간 밑줄 : `.formLogin` 또는 `.logout`처럼 메서드 이름만 입력했을 경우 빨간 밑줄이 나오는 이유는 메서드들의 매개변수를 받아야만 완전한 메서드 호출로 인정하기 때문이다. 매개변수를 넣지 않으면, 이 메서드들은 호출되지 않은 상태로 남아 있기 때문에 에러로 인식한다.
2. 람다 표현식의 의미와 문법
    * 람다 표현식은 Java8 부터 도입된 문법으로, 메서드를 간결하게 표현할 수 있는 방법이다. 
    *  람다 표현식의 기본 형태는 아래와 같다.
    ```java
    (parameters) -> (expression)
    ```
   이 문법은 인터페이스의 메서드를 구현하는 익명 클래스의 축약형이라고 이해하면 좋다.
3. `.formLogin(변수명 -> 변수명)`의 의미
   * `.formLogin(formLogin -> formLogin)` 이라는 코드는 실제로 다음과 같은 의미를 가지고 있다.
    ```java
    .formLogin(formLogin -> formLogin.loginPage("/lgoin").defaultSuccessUrl("/articles", true))
    ```
   여기서 `formLogin`은 매개변수 이름으로, 개발자가 원하는 이름으로 지정할 수 있다. `formLogin`이란 매개변수는 `FormLoginConfigurer` 객체를 가리키는 것으로, 람다 표현식 안에서 `formLogin` 변수를 통해 `FormLoginConfigurer` 객체의 메서드들을 호출할 수 있다.
    같은 원리로 `.logout(logout -> logout)` 또한 `LogoutConfigurer` 객체의 설정을 위한 람다 표현식이다.
---
### `WebSecurityConfig` 클래스에서 사용된 `authenticationManager` 메서드에 대하여
```
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailService userDetailService) throws Exception {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService); // 1. 사용자 정보 서비스 설정
        authProvider.setPasswordEncoder(bCryptPasswordEncoder);

        return new ProviderManager(authProvider);
    }
```
1. 역할 : 스프링 시큐리티의 `AuthenticationManager`를 구성하는 것으로 이 매니저는 인증 절차를 처리하며, 주어진 사용자 정보를 검증하는 책임을 갖는다.
2. 구성요소 
   * `DaoAuthenticationProvider` : 데이터베이스에서 사용자 정보를 조회하고, 패스워드를 검증하는 데 사용
   * `UserDetailService` : 사용자 정보를 로드하는 서비스로 이 서비스를 통해 사용자 정보가 `DaoAuthenticationProvider`에 제공된다.
   * `BCryptPasswordEncoder` : 패스워드 인코딩을 위한 인코더로 사용자 패스워드를 해시하고 비교할 때 사용된다. 
3. 코드 리뷰
    * `DaoAuthenticationProvider` 설정 : `userDetailService`를 설정하여 사용자 정보를 제공하고, `BCryptPasswordEncoder`를 설정하여 패스워드 검증을 처리한다.
    * `ProviderManager` 반환 : `DaoAuthenticationProvider`를 통해 인증 과정을 처리하는 `ProviderManager`를 반환한다. 이 매니저는 여러 인증 공급자들을 관리하며, `DaoAuthenticationProvider`를 통해 사용자 정보를 검증한다.
---
### `SecurityContextLogoutHandler`와 로그인 관련 보안 기능에 대하여
1. 로그인 관련 보안 기능
   * `SecurityFilterChain`
     * 여러 보안 필터를 체인 형태로 연결하여 `HTTP` 요청을 처리하는 보안 설정을 구성한다. 각 필터는 요청을 검사하고, 인증 및 인가를 처리한다. 따라서 로그인, 로그아웃, 인증, 인가 등 다양한 보안 설정을 포함하고 있는 전체 보안 구성을 관리한다.
   *  `UsernamePasswordAuthenticationFilter` 
     * 로그인 폼에서 사용자 ID와 비밀번호를 제출할 때, 해당 요청을 처리하여 인증을 시도한다. 
     * 사용자가 로그인 폼을 제출할 때, 이 필터가 요청을 가로채고 사용자의 인증을 처리한다.
   * `AuthenticationManager`
     * 인증 요청을 받아서 사용자의 자격 증명을 확인하고 인증을 수행한다.
     * 로그인 요청을 처리하는 핵심 컴포넌트로 인증을 시도하여 성공 여부를 결정한다.
   * `DaoAuthenticationProvider`
     * 사용자 정보를 데이터베이스에서 조회하여 인증을 수행한다.
     * 사용자 정보를 데이터베이스에서 검색하고, 비밀번호를 검증하여 인증을 처리한다.
   * `CustomAuthenticationManager`
     * `AuthenticationManager`의 구현체로 특정 요구 사항에 맞춘 인증 로직을 정의할 수 있다. 

2. `SecurityContextLogoutHandler`
    * 로그아웃을 처리하는 도구이다. 사용자가 로그아웃할 때, 현재 세션을 무효화하고 사용자의 인증 정보를 지우는 역할을 한다.
    * 주요 메서드
        * `logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication`) : 사용자가 로그아웃할 때 호출된다.
            * `HttpServletRequest request` : 사용자의 요청 정보
            * `HttpServletResponse response` : 서버의 응답 정보
            * `Authenticaton authentication` : 현재 인증 정보