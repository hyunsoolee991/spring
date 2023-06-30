package com.zcarc.book.springboot2webservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// @WebMvcTest 에서 스캔할 때 @EnableJpaAuditing를 @SpringBootApplication와 함께 스캔을 한다.
// @EnableJpaAuditing는 최소 하나의 @Entity가 필요하지만 @WebMvcTest는 당연하게도 없다. 그래서 이 둘을 분리한다.
// JapConfig로 이동
// @EnableJpaAuditing
@SpringBootApplication
public class Springboot2WebserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(Springboot2WebserviceApplication.class, args);
    }

}
