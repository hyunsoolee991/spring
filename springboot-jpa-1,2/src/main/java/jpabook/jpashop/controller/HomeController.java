package jpabook.jpashop.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j // Logger를 사용하기 위해서 코드로 작성하는 로직을 롬복 애노테이션으로 대체했다.
public class HomeController {


    @RequestMapping("/")
    public String home() {
        log.info("home controller");
        return "home";
    }
}
