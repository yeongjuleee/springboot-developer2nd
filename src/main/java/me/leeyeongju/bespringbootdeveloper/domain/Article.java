package me.leeyeongju.bespringbootdeveloper.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
    DB와 연결되는 DAO : domain
 */

@Entity // Entity 지정
@Getter @NoArgsConstructor // 필드의 값을 가져오는 게터 메서드들을 Getter 어노테이션과 NoArgsConstructor 어노테이션으로 대체
public class Article {

    @Id // id 필드를 PK로 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PK를 자동으로 1씩 증가
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "title", nullable = false) // title 이라는 not null 컬럼과 매핑
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Builder // 빌더 패턴으로 객체 생성
    public Article(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // 엔티티에 요청받은 내용으로 값을 수정하는 update() 메서드
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    /*
    @NoArgsConstructor 어노테이션을 이용하여 접근 제어자가 protected인 기본 생성자를 별도의 코드 없이 생성
    @Getter 어노테이션을 이용하여 클래스 필드에 대해 별도의 코드 없이 모든 필드에 대한 접근자 메서드를 생성
    => 코드의 가독성 향상

    public void update() : 엔티티에 요청받은 내용으로 값을 수정하는 메서드로 특정 아이디의 글을 수정할 수 있도록 생성자 생성
     */
}
