package me.leeyeongju.bespringbootdeveloper.service;

import me.leeyeongju.bespringbootdeveloper.domain.Article;
import me.leeyeongju.bespringbootdeveloper.dto.AddArticleRequest;
import me.leeyeongju.bespringbootdeveloper.dto.UpdateArticleRequest;
import me.leeyeongju.bespringbootdeveloper.repository.BlogRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class BlogServiceTest {
    /*
    `BlogService`에 정의된 각 메서드를 개별적으로 테스트 하는 테스트 코드이다.
        save() : 글을 생성(추가)하는 메서드
        findAll() : 모든 글을 가져오는 메서드
        findById() : 글을 하나만 상세조회하는 메서드
        delete() : 글을 삭제하는 메서드
        update() : 글을 수정하는 메서드

    `BlogServiceTest`의 역할은 서비스 계층을 테스트하는 것으로 HTTP 요청이나 웹 관련 기능을 다루지 않고, 비즈니스 로직과 테이터베이스 작업을 잘 수해앟는지 확인을 하는 것이다. 때문에 `MockMvc` 와 같은 HTTP 요청 관련 도구는 필요하지 않고 직접 `BlogService`의 메서드를 호출하여 해당 동작이 올바르게 작동하는지 검증한다.
     */

    @Autowired
    private BlogService blogService;

    @Autowired
    private BlogRepository blogRepository;

    @BeforeEach // 테스트 실행 전 실행 되는 것으로 해당 테스트에서는 데이터베이스를 초기화하는 방법으로 이용
    void setUp() {
        blogRepository.deleteAll();
    }

    @DisplayName("save : 블로그 글 생성에 성공한다.")
    @Test
    void saveArticle() {
        // given : 테스트 글 작성의 초기값 설정
        final String title = "title";
        final String content = "content";

        AddArticleRequest request = new AddArticleRequest(title, content); // 값을 전달받는 AddArticleRequest 를 이용하여 값을 전달받고, 저장한 새로운 객체 request를 생성한다.

        // when : 받은 내용을 바탕으로 blogServe 의 구현한 save 메서드를 이용한다. => 글을 생성한다.
        Article article = blogService.save(request);

        // then : 입력 받은 값과 동일한지 검증한다.
        assertThat(article.getTitle()).isEqualTo(title);
        assertThat(article.getContent()).isEqualTo(content);

        // isPresent() : 값이 있으면 true 반환, 없으면 false 반환 => 글이 실제로 데이터베이스에 저장되었는지 확인한다.
        assertThat(blogRepository.findById(article.getId())).isPresent();
    }

    @DisplayName("findAll() : 작성된 모든 글 목록을 조회하는데 성공한다.")
    @Test
    void findAllArticles() {
        // given : 테스트를 위한 기본 글 설정(목록이기 때문에 여러개를 생성)
        Article firstArticle = blogRepository.save(Article.builder()
                .title("First Title")
                .content("First Content")
                .build());

        Article secondArticle = blogRepository.save(Article.builder()
                .title("Second Title")
                .content("Second Content")
                .build());

        Article thirdArticle = blogRepository.save(Article.builder()
                .title("Third Title")
                .content("Third Content")
                .build());

        // when : 모든 글을 조회하는 메서드 호출
        List<Article> articles = blogService.findAll();

        // then : 조회된 글 목록이 3개인지, 저장한 글과 동일한지 확인하는 검증
        assertThat(articles).hasSize(3); // 글 개수가 3개인지 확인
        assertThat(articles.get(0).getTitle()).isEqualTo(firstArticle.getTitle()); // 첫 번째 글의 제목 확인
        assertThat(articles.get(1).getTitle()).isEqualTo(secondArticle.getTitle()); // 두 번째 글의 제목 확인
        assertThat(articles.get(2).getTitle()).isEqualTo(thirdArticle.getTitle()); // 세 번째 글의 제목 확인

    }

    @DisplayName("findArticle : 글 상세보기에 성공한다")
    @Test
    void findArticle() {
        // given : 블로그 글 저장
        final String title = "title";
        final String content = "content";

        Article savedArticle = blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());

        // savedArticle의 ID를 가지고 온다.
        long id = savedArticle.getId();

        // when : 저장된 글을 ID로 조회한다.
        Article foundArticle = blogService.findById(id);

        // then : 조회한 글이 실제로 저장된 글과 같은지 확인한다.
        assertThat(foundArticle.getId()).isEqualTo(savedArticle.getId());
        assertThat(foundArticle.getTitle()).isEqualTo(savedArticle.getTitle());
        assertThat(foundArticle.getContent()).isEqualTo(savedArticle.getContent());

        // when & then : 존재하지 않는 ID로 조회할 경우 예외 발생 여부 확인
        final long nonExistentId = 999L;
        assertThrows(IllegalArgumentException.class, () -> {
            blogService.findById(nonExistentId);
        });
    }

    @DisplayName("findArticle() : 존재하지 않는 ID로 글을 조회할 경우 예외를 발생시킨다.")
    @Test
    void findArticleNotFound() {
        //given : 존재하지 않는 ID 설정
        final long nonExistentId = 999L;

        // when & then : 존재하지 않는 ID로 조회할 경우 예외 발생 여부 확인
        assertThrows(IllegalArgumentException.class, () -> {
            blogService.findById(nonExistentId);
        });
    }

    @DisplayName("deleteArticle : 작성된 글 삭제에 성공한다")
    @Test
    void deleteArticle() {
        // given : 글 삭제 전 생성되어있어야 하는 글 설정
        final String title = "title";
        final String content = "content";

        Article savedArticle = blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());

        // when : 글 삭제하는 메서드 호출
        blogService.delete(savedArticle.getId());

        // then : 글이 삭제되었는지 확인하기
        assertThat(blogRepository.findById(savedArticle.getId())).isNotPresent(); // 값이 없는지 확인하는 isNotPresent()
    }

    @DisplayName("updateArticle : 글 수정 동작에 성공한다 ")
    @Test
    void updateArticle() {
        // given : 테스트를 위한 글 작성
        final String title = "title";
        final String content = "content";

        Article savedArticle = blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());

        // 수정한 글을 저장할 값 설정
        final String modifyTitle = "modify Title";
        final String modifyContent = "modify Content";
        UpdateArticleRequest request = new UpdateArticleRequest(modifyTitle, modifyContent); // UpdateArticleRequest(DTO)를 이용하여 수정할 내용을 전달받아 수정한다.

        // when : 글 수정 메서드 호출
        Article updatedArticle = blogService.update(savedArticle.getId(), request);

        // then : 수정한 내용이 맞는지 검증한다.
        assertThat(updatedArticle.getTitle()).isEqualTo(modifyTitle);
        assertThat(updatedArticle.getContent()).isEqualTo(modifyContent);
    }

}