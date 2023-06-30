package com.zcarc.book.springboot2webservice.web;

import com.zcarc.book.springboot2webservice.web.dto.HelloResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/hello/dto")
    public HelloResponseDto hello(@RequestParam("name") String name,
                                  @RequestParam("amount") int amount) {

        return new HelloResponseDto(name, amount);
    }
}
