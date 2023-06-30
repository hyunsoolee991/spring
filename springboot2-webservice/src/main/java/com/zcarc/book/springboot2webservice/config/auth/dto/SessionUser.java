package com.zcarc.book.springboot2webservice.config.auth.dto;

import com.zcarc.book.springboot2webservice.domain.user.User;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable { // 직렬화를 구현해야하므로 SessionUser 를 따로 생성

    private String name;
    private String email;
    private String picture;

    public SessionUser(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.picture = user.getPicture();
    }
}
