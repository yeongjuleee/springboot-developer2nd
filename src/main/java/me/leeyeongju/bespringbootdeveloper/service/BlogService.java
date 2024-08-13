package me.leeyeongju.bespringbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import me.leeyeongju.bespringbootdeveloper.domain.Article;
import me.leeyeongju.bespringbootdeveloper.dto.AddArticleRequest;
import me.leeyeongju.bespringbootdeveloper.repository.BlogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor // final이 붙거나 @NotNull이 붙은 필드의 생성자 추가
@Service // 빈으로 등록
public class BlogService {

    private final BlogRepository blogRepository;

    // 블로그 글 추가 메서드
    public Article save(AddArticleRequest request) {
        return blogRepository.save(request.toEntity());
    }

    // 모든 글을 가져오는 메서드
    public List<Article> findAll() { // 목록으로 가져오기 위해 List 형식으로 가져옴
        return blogRepository.findAll();
    }

    // 글 하나만 조회하는 메서드
    public Article findById(long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    /*
    @RequiredArgsConstructor : 빈을 생성자로 생성하는 롬복에서 지원하는 어노테이션. final 키워드나 @NotNull 이 붙은 필드로 생성자를 만들어 준다.
    @Service : 클래스를 빈으로 서블릿 컨테이너에 등록해준다.

    save() : JpaRepository에서 지원하는 저장 메서드 save()로 AddArticleRequest 클래스에 저장된 값들을 article 데이터 베이스에 저장한다.
    findAll() : Jpa 지원 메서드 findAll()을 호출해 article 테이블에 저장되어 있는 모든 데이터를 조회한다.
    findById() : JPA에서 제공하는 findById() 메서드를 사용하여 ID를 받아 엔티티를 조회하고 없으면 IllegalArgumentException 예외를 발생한다.
     */
}
