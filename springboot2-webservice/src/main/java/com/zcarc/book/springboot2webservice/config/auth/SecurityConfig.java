package com.zcarc.book.springboot2webservice.config.auth;

import com.zcarc.book.springboot2webservice.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@EnableWebSecurity // 스프링 시큐리티 설정 활성화
public class SecurityConfig  {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .headers().frameOptions().disable()  // h2-console 화면을 사용하기 위해서 해당 옵션들을 disable
                .and()
                    .authorizeRequests() // URL 별 권한 관리 설정 옵션 시작점, 이 메서드가 선언되어야만 antMatchers 사용 가능
                    .antMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**", "/profile").permitAll() // 권한 관리 대상 지정 옵션 / URL, HTTP 메서드별로 관리 가능 / 지정된 URL들은 permitAll() 옵션으로 전체 열람 권한 부여
                    .antMatchers("/api/v1/**").hasRole(Role.USER.name()) // Role.USER.name() 권한을 가진 사람만 열람 가능
                    .anyRequest().authenticated() // anyRequest(): 설정된 값 이외의 나머지 URL 은 authenticated() 의 사용자에게 권한 부여 / authenticated(): 인증된 사용자, 로그인한 사용자들만 허용가능
                .and()
                    .logout()
                        .logoutSuccessUrl("/") // 로그아웃 성공 시, 해당 URL 로 이동
                .and()
                    .oauth2Login()
                        .userInfoEndpoint()
                            .userService(customOAuth2UserService);
                // oauth2Login() : OAuth2 로그인 기능에 대한 여러 설정의 진입점
                // userInfoEndpoint() : OAuth2 로그인 성공 이후 사용자 정보를 가져올 떄의 설정을 담당
                // userService() : 소셜 로그인 성공 시 후속 조치를 진행항 UserService 의 구현체를 등록 / 리소스 서버(소셜 서비스)에서 사용자 정보를 가져온 상태에서 추가로 진행하고자 하는 기능 명시 가능

        return http.build();
    }
}
