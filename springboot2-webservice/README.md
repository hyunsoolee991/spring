# 스프링 부트와 AWS로 혼자 구현하는 웹 서비스

### 관계형 데이터베이스

- **어떻게 데이터를 저장**할지에 초점에 맞춰진 기술이다.

### 객체지향 프로그래밍 언어

- 메세지를 기반으로 **기능과 속성을 한 곳에서 관리**하는 기술이다.

### JPA

- 정의
  - 인터페이스로서 자바 표준명세서이다.
  - JPA를 사용하기 위해서는 구현체가 필요하다. 대표적으로 Hibernate가 있다. 
- 사용하는 이유
  - RDB와 OOP의 **중간에서 패러다임을 일치** 시켜주는 기술이다. 
  - JPA를 사용하지 않으면 애플리케이션 개발을 데이터베이스 모델링에만 집중하게 된다. JPA는 이 문제를 해결해준다.
  - 개발자는 객체지향적으로 프로그래밍하고, JPA가 이를 관계형 데이터베이스에 맞게 SQL을 대신 생성해서 실행한다. 
  - 객체지향적으로 프로그래밍할 수 있으니 SQL에 종속적인 개발을 하지 않아도 된다. 
  - 객체 중심으로 개발을 하면 생산성 향상과 유지보수가 편해진다.
  - RDB를 객체지향으로 표현하는게 쉽지 않다. 
  - 객체를 DB에 저장하려고 하면 여러 문제가 발생한다.
- 이러한 장점들로 대규모 365일 24시간 트래픽과 데이터를 가진 서비스에서 JPA는 표준 기술로 자리잡고 있다.

### Spring Data JPA

- 정의
  - JPA 구현체들을 좀 더 쉽게 사용하고자 추상화 시킨 모듈이다.
- 관계
  - JPA <- Hibernate <- Spring Data JPA
- 사용하는 이유
  - JPA의 구현체를 한 단계 더 감싸놓은 Spring Data JPA의 등장이유는 크게 두 가지가 있다.
    - 구현체 교체의 용이성
    - 저장소 교쳐의 용이성
  - 구현체 교체의 용이성
    - **Hibernate 외에 다른 구현체로 쉽게 교체하기 위함**이다. (Spring Data JPA 내부에서 구현체 매핑을 지원해준다.)
    - Hibernate가 언젠간 수명을 다해서 새로운 JPA 구현체가 대세로 떠오를 때, Spring Data JPA를 사용하고 있다면 아주 쉽게 교체할 수 있다.
  - 저장소 교체의 용이성
    - **관계형 데이터베이스 외에 다른 저장소로 쉽게 교체하기 위함**이다.
    - 서비스 초기에는 관계형 데이터베이스로 모든 기능을 처리했지만, 트래픽이 많아져 관계형 데이터베이스로 감당이 안될 때 MongDB로 교체가 필요하다면, 
      Spring Data JPA에서 Spring Data MongDB로 의존성 교체만 하면 된다. (Spring Data의 하위 프로젝트들은 기본적인 CURD의 인터페이스가 같기 때문이다.)
    - Spring Data의 하위 프로젝트들 Spring Data JPA, Spring Data Redis, Spring Data MongDB 등등 은 save(), findAll(), findOne() 등을 인터페이스로 갖고 있다.
      그래서 저장소가 교체되어도 기본적인 기능들을 변경하지 않아도 된다.
- Hibernate와 Spring Data JPA를 쓰는 것 사이에는 큰 차이가 없다. 그럼에도 Spring에서는 이러한 장점들로 Spring Data 프로젝트 사용을 권장하고 있다.

### 도메인 (Domain)

- 게시글, 댓글, 회원, 정산, 결제 등 소프트웨어에 대한 요구사항 혹은 문제영역이다.

### 애노테이션

- @SpringBootTest
  - 별다른 설정이 없다면 H2 DB를 실행한다.
  - webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
    - 실제 HTTP 서버를 띄운다.
    - RestTemplate 또는 TestRestTemplate 와 함께 사용한다.
  - webEnvironment = SpringBootTest.WebEnvironment.MOCK
    - 실제 HTTP 서버를 띄우지 않는다.
- @WebMvcTest
  - JPA가 동작하지 않는다.
  - JPA를 사용하려면 @SpringBootTest 와 TestRestTemplate 를 사용하면 된다.
- @MappedSuperclass
  - JPA Entity 클래스들이 이 애노테이션을 사용하는 A 엔티티 클래스를 상속할 경우 A 엔티티 클래스의 필드도 칼럼으로 인식하도록 한다.
- @EntityListeners(AuditingEntityListener.class)
  - 엔티티 클래스에 Auditing 기능을 포함시킨다.
- @CreatedDate
  - Entity 가 생성되어 저장될 때 시간이 자동으로 저장된다.
- @LastModifiedDate
  - 조회한 Entity 의 값이 변경될 때 시간이 자동으로 저장된다.
- @EnableJpaAuditing
  - JPA Auditing 을 활성화한다. 
- @Query
  - Spring Data JPA 에서 지원하지 않는 메서드에 명시해서 직접 쿼리를 작성할 수 있다.

### 데이터베이스

- H2
  - MySQL 쿼리도 동작한다. (출력되는 쿼리 로그를 MySQL 버전으로 볼 수 있다.)

### Service

- 많은 사람들이 오해하고 있는 것이, Service에서 비즈니스 로직을 처리해야 한다는 것이다.
- **Service는 트랜잭션, 도메인 간 순서 보장**의 역할만 한다.

### Spring 웹 계층

- Web Layer
- Service Layer
- Repository Layer
- DTOs
- Domain Model

### 비즈니스 처리

- 웹 계층에서 비즈니스 처리를 해야할 곳은 도메인 모델 이다.
- 기존에 서비스로 비즈니스 처리를 하는 방식을 트랜잭션 스크립트라고 한다.
- 트랜잭션 스크립트
  - 모든 로직이 서비스 클래스 내부에서 처리된다. 그래서 서비스 계층이 무의미하며, 객체란 단순히 데이터 덩어리 역할만 하게 된다.
- 도메인 모델 
  - 서비스 메서드는 트랜잭션과 도메인 간의 순서만 보장해준다.

### 추천 도서
- 도메인
  - DDD start(지앤선, 2016) - 최범균

### 추천 강의
- TDD
  - [클린코더스](https://www.youtube.com/watch?v=60lLSe1phks&list=PLagTY0ogyVkIl2kTr08w-4MLGYWJz7lNK&index=1) - 백명석

### 추천 아티클
- CI
  - [Continuous Integration (original version)](https://www.martinfowler.com/articles/originalContinuousIntegration.html) - 마틴 파울러