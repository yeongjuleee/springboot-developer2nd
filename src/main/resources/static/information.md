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
