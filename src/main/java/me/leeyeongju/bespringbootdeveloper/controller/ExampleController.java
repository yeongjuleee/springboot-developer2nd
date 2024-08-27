package me.leeyeongju.bespringbootdeveloper.controller;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;

@Controller // 컨트롤러라는 것을 명시적으로 표시한다.
public class ExampleController {
    /*
    타임리프 문법 공부를 위한
    /thymeleaf/example 에 GET 요청이 오면 특정 데이터를 뷰(HTML)로 넘겨주는 모델 객체에 추가한느 컨트롤러 메서드
     */

    @GetMapping("/thymeleaf/example")
    public String thymeleafExample(Model model) {
        // 뷰로 데이터를 넘겨주는 모델 객체
        Person examplePerson = new Person();
        examplePerson.setId(1L);
        examplePerson.setName("홍길동");
        examplePerson.setAge(11);
        examplePerson.setHobbies(List.of("운동", "독서"));

        model.addAttribute("person", examplePerson); // Person 객체에 저장
        model.addAttribute("today", LocalDate.now());

        return "example"; // example.html 라는 뷰 조회
    }

    @Getter @Setter
    class Person{
        private Long id;
        private String name;
        private int age;
        private List<String> hobbies;
    }
}
