package com.zcarc.book.springboot2webservice.config.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER) // 이 애노테이션이 생성될 수 있는 위치는 파라미터가 된다.
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginUser { // 이 파일을 애노테이션으로 지정한다.

}
