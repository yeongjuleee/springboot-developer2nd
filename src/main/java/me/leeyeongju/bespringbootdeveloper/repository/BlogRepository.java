package me.leeyeongju.bespringbootdeveloper.repository;

import me.leeyeongju.bespringbootdeveloper.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Article, Long> {
    // JpaRepository 클래스를 상속받을 때 엔티티 Article과 엔티티의 PK 타입 Long을 인수로 넣어 사용할 수 있도록 함
    /*
    BlogRepository 구성 :
    JpaRepository를 상속받음. JpaRepository의 부모 클래스의 CrudRepository에 Save() 메서드가 선언이 되어 있다.
    save() 메서드를 사용하면 DB에 Article 엔티티를 저장할 수 있다.
     */
}
