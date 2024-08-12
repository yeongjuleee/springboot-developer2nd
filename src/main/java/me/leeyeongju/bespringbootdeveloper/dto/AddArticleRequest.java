package me.leeyeongju.bespringbootdeveloper.dto;

/*
블로그 글 추가 서비스 계층 작성을 위한 서비스 계층에서 요청을 받을 객체 AddArticleRequest 객체 생성
(View 에서 값을 전달하기 위한 객체로 Data Transfer Object)

DAO 는 DB와 연결되고 데이터를 조회하고, 수정하는 데 사용하는 객체로 데이터 수정과 관련된 로직이 포함되지만 DTO는 단순하게 데이터를 옮기기 위해 사용하는 전달자 역할을 하는 객체로 비즈니스 로직을 포함하지 않음
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.leeyeongju.bespringbootdeveloper.domain.Article;

@NoArgsConstructor // 기본 생성자 추가
@AllArgsConstructor // 모든 필드 값을 파라미터로 받는 생성자 추가
@Getter
public class AddArticleRequest {
    
    private String title;
    private String content;
    
    /*
    toEntity() : 빌더 패턴을 사용해 DTO를 엔티티로 만들어주는 메서드로 추후 블로그 글을 추가할 때 저장할 엔티티로 변환하는 용도

    AddArticleRequest 는 클라이언트가 입력한 값을 서버가 받는 객체이다.
     */
    
    public Article toEntity() {
        // 생성자를 사용해 객체 생성
        return Article.builder()
                .title(title)
                .content(content)
                .build();
    }
}
