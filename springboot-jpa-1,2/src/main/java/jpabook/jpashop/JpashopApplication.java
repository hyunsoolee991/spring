package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpashopApplication {

    public static void main(String[] args) {
        SpringApplication.run(JpashopApplication.class, args);
    }

    // 프록시 객체가 JSON으로 변환되는 것을 무시하기 위해서 라이브러리 등록
    // 프록시 객체가 비어있는 상태라면 변환하지 못하지만, 프록시 객체에 값이 있다면 무시하지 않는다.
    // @Bean
    // Hibernate5Module hibernate5Module() {
    //     return new Hibernate5Module();
    // }

    // Lazy loading를 무시하고 강제로 로딩
    @Bean
    Hibernate5Module hibernate5Module() {
        Hibernate5Module hibernate5Module = new Hibernate5Module();
        // Lazy loading 이 있는 모든 객체를 가져와서 이 옵션은 사용하면 안된다.
        // hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true);
        return hibernate5Module;
    }

}
