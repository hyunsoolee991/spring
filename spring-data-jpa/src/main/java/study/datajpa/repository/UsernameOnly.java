package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {

    // 클로즈 프로젝션: @Value 없이 선언

    // 오픈 프로젝션: @Value 로 가져올 필드를 지정
    // DB 에서 모든 컬럼들 select 하고 애플리케이션 상에서 원하는 필드만 선택해서 가져온다.
    @Value("#{target.username + ' ' + target.age}")
    String getUsername();
}
