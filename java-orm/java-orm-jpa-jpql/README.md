# 자바 ORM 표준 JPA 프로그래밍

## JPA 서브쿼리

FROM절 서브쿼리는 현재 JPQL에서 불가능

- 조인으로 풀 수 있으면 풀어서 해결
  - 1순위 JOIN 사용
  - 2순위 쿼리 2번을 실행
  - 3순위 쿼리 2번으로 안되는 경우 네이티브 쿼리 작성

## 경로 표현식

묵시적 조인

- 단일 값 또는 컬렉션 값 연관 경로에서 발생
- 항상 내부조인 발생
- 실무에서 권장 X

명시적 조인

- 명시적으로 원하는 조인 가능
- 실무에서 권장 O

## N + 1

- 첫번째 쿼리 한 번을 날린 result 결과만큼 N번 쿼리를 실행하는 것을 말함 <br />
- ex) 회원이 100명 있으면 조회 쿼리 한 번에 100명의 결과인 100만큼 다시 100번 쿼리가 실행됨
  - 각 각의 회원이 모두 팀이 다르다면 그 팀을 찾기 위해서 쿼리가 100번 실행됨
- 즉시로딩, 지연로딩에서 모두 발생
- 이 문제의 해결은 join fetch를 사용
- 지연로딩과 join fetch를 동시에 사용하면 우선순위는 join fetch

## 페치 조인 특징과 한계

- 페치 조인 대상에서 별칭 사용은 가급적 안하는 것이 좋다.
  - ```sql
    String query = "select t from Team t join fetch t.members as m";
    ```
- 데이터의 정합성이나 객체 그래프 사상이 별칭을 통한 탐색이 맞지 않다.
- 객체 그래프 탐색을 통해서 조건식 등을 사용하면 위험하다.
- 단순히 엔티티를 조인하여 동시로딩할 때만 사용하는 것이 좋다.

## 페치 사이즈 설정

- select 할 때 쿼리가 여러 번 실행돼서 쿼리 한 번에 가져오고 싶을 때 사용
- persistence.xml에서 hibernate.default_batch_fetch_size로 사이즈 설정
- @BatchSize 애노테이션으로 사이즈 설정
