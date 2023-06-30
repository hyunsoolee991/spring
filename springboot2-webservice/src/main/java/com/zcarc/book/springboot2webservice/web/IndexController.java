package com.zcarc.book.springboot2webservice.web;

import com.zcarc.book.springboot2webservice.config.auth.LoginUser;
import com.zcarc.book.springboot2webservice.config.auth.dto.SessionUser;
import com.zcarc.book.springboot2webservice.web.dto.PostsResponseDto;
import com.zcarc.book.springboot2webservice.service.PostsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Controller
public class IndexController {

    private final PostsService postsService;
    private final HttpSession httpSession;


    @GetMapping("/")
    public String index(Model model, @LoginUser SessionUser user) {

        model.addAttribute("posts", postsService.findAllDesc());

        // CustomOAuth2UserService 에서 로그인 성공 시 세션에 SessionUser 를 저장하도록 구성했다.
        // 로그인 성공 시 httpSession.getAttribute("user") 에서 값을 가져올 수 있다.
        // SessionUser user = (SessionUser) httpSession.getAttribute("user");

        // 세션에 저장된 값이 있을 때만 user 의 name 을 model 에 "userName" 으로 등록한다.
        // 세션에 저장된 값이 없으면 model 에는 값이 없는 상태이니 로그인 버튼이 보이지 않는다.
        if (user != null) {
            model.addAttribute("userName", user.getName());
        }

        return "index";
    }

    @GetMapping("/posts/save")
    public String postsSave() {
        return "posts-save";
    }

    @GetMapping("/posts/update/{id}")
    public String postsUpdate(@PathVariable Long id, Model model) {

        PostsResponseDto dto = postsService.findById(id);
        model.addAttribute("post", dto);

        return "posts-update";
    }


}
