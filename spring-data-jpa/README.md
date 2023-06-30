# 스프링 데이터 JPA

### 단축키

- 파일 내에서 특정 이름 찾기 : Command + F12

### JpaRepository 인터페이스의 구현체가 없는데 동작하는 이유?

- 해당 인터페이스를 상속 클래스는 프록시 객체가 된다.
- 스프링 데이터 JPA가 구현 클래스를 자동으로 만들어서 주입한다.
- 스프링 데이터 JPA가 자바의 기본적인 프록시 기술로 객체를 만들어서 주입한다.
- @Repository 애노테이션이 필요없다.

### 애노테이션

- @Repository
  - 컴포넌트 스캔
  - JPA의 예외를 스프링에서 공통적으로 처리할 수 있는 예외로 변환하는 기능 포함
- @NamedQuery
  - 쿼리에 이름을 지정해서 이름으로 SQL 을 실행할 수 있다.
  - 실무에서 사용을 거의 하지 않는다.
  - createQuery로 JPQL을 작성하고 잘못된 SQL을 입력해도 문자라서 오류 없이 정상적으로 동작하는데 사용자가 직접 해당 SQL을 실행하는 기능을 사용하면 그 때 오류가 발생한다. <br />
    반면에 NamedQuey는 애플리케이션 로딩 시점에 JPQL을 SQL로 파싱해서 문법 오류를 찾을 수 있다는 큰 장점이 있다. <br />
    파싱이 안된다는 것은 실제 기능을 쓰기 전 까지는 해당 문법의 오류에 대한 여부를 모른다는 의미이다. <br />
- @Query
  - name 속성이 있다면 값은 @NamedQuery 쿼리의 name 과 매칭된다.
  - @Query 애노테이션이 없어도 username과 일치하는 NamedQuery를 자동으로 찾는다. 만약 NamedQuery가 없다면 메소드 쿼리가 실행된다. 순서는 바꿀 수 있다.
  - name 속성 없이 JPQL을 바로 작성할 수 있다. 실무에서 많이 사용한다.
  - JPQL로 작성하면 애플리케이션 로딩 시점에 SQL로 파싱하여 오류를 찾을 수 있다.
  - countQuery를 직접 작성하지 않으면 left join이 필요하지 않은 경우에 쿼리에 포함되어 복잡한 쿼리인 경우 성능에 문제가 생길 수 있다.
- @Param
  - 쿼리에 파라미터를 넘길 때 사용한다.
- @Modifying
  - @Query 애노테이션으로 JPQL을 작성할 때, 수정 쿼리로 작성한다면 필요하다.
  - 쿼리 실행 후 영속성 컨텍스트를 자동으로 clear 해주는 clearAutomatically 속성이 있다.
- @EntityGraph
  - 오버라이드한 메소드 쿼리에서도 사용할 수 있다.
  - @NamedEntityGraph 의 이름을 사용할 수 있다.
  - JPA 표준 스펙이다.
  - 쿼리가 간단할 때 사용한다.
  - 쿼리가 이미 복잡하면 이 애노테이션 대신 JPQL 을 사용한다.
- @NamedEntityGraph
  - @EntityGraph 를 이름을 지정한 것과 같다.
  - 실무에서 거의 사용을 하지 않는다.
  - JPA 표준 스펙이다.
- @QueryHints
  - readOnly 로 속성을 지정하면 스냅샷을 만들지 않고 읽기 전용으로 객체를 만든다.
  - 성능 문제가 된다면 사용을 고려해보는 것도 좋다.
  - 실무에서 많이 사용하지 않는다.
- @Lock
  - 실무에서 많이 사용하지 않는다.
  - 실시간 트래픽이 많으면 락을 걸면 안된다. 만약 락을 건다면 옵티미스틱 락 등을 사용하는 것이 좋다.
  - 실시간 트래픽이 많지 않으면 돈과 관련된 트래픽이라면 패스매스틱 락 등을 사용하는 것도 좋은 방법이다.
- @Column
  - updatable = false 을 설정하면 업데이트가 되지 않는다.
- @MappedSuperClass
  - JPA 에서는 진짜 상속, 속성만 상속한 클래스가 있다.
  - @MappedSuperClass 는 속성만 상속하는 클래스가 된다.
- @Transactional
  - 이미 트랜잭션이 있는 상태에서 또 트랜잭션을 만나면 기존의 트랜잭션을 이어 받는다.
  - readOnly = true 로 설정되어 있으면 flush 를 생략한다.
- @GeneratedValue
  - JPA가 em.persist 메서드를 호출하고 나면 그 때 value가 들어간다. 그 전까지는 id가 생기지 않는다.
  - 실무에서 기본적으로 사용은 하지만 프로젝트가 크고 데이터가 많으면 id를 직접 생성해야 할 때가 있다. 그럴때는 Persistable 인터페이스를 구현해야 한다.

### 파라미터 바인딩

- 위치 기반은 거의 사용하지 않는다. 위치가 바뀌면 오류가 생길 수 있다.
- 이름 기반이 가독성이나 유지보수가 더 좋아서 실무에서 많이 사용한다.

### 리턴 타입

- List는 데이터가 없으면 비어있는 컬렉션을 반환 해준다.

  - ```java
    List<Member> result = memberRepository.findList123ByUsername("asdasd");
    System.out.println("result = " + result);
    System.out.println("result.size() = " + result.size());
    // 결과: [], 0
    ```

- 단건 조회는 결과가 없으면 null 을 반환 한다.

  - JPA는 결과가 없으면 NoResultException 이 발생하는데 스프링 데이터 JPA는 null 을 반환해준다.
  - 내부적으로 try-catch 로 감싸서 예외가 발생하면 null 을 반환하도록 작성되어 있다.
  - ```java
    Member findMember = memberRepository.findMemberByUsername("asdasd");
    System.out.println("findMember = " + findMember);
    // 결과: null
    ```

- DB 를 조회했는데 데이터가 없을 수도 있다면 Optional 을 사용하는 것을 권장한다.

  - ```java
    Optional<Member> findMemberOpt = memberRepository.findOptionalByUsername("asdasd");
    System.out.println("findMemberOpt = " + findMemberOpt);
    // 결과: Optional.empty
    ```

- 단건 조회인데 결과가 2개 이상이면 Optional 이라도 예외가 발생한다.
  - 원래는 javax.persistence.NonUniqueResultException 예외가 발생하는데 스프링 데이터 JPA가 org.springframework.dao.IncorrectResultSizeDataAccessException 로 변환해서 예외를 반환한다.
    변환하는 이유는 리파지토리의 기술은 JPA 또는 몽고DB 등 다른 기술이 될 수 있다. 이걸 사용하는 서비스 계층의 클라이언트는 JPA에 의존하는게 아니라
    스프링이 추상화하는 예외에 의존하면 하부의 리파지토리 기술을 JPA, 몽고DB 등 다른 기술로 바꿔도 스프링은 동일하게 IncorrectResultSizeDataAccessException 예외를 반환한다.
    그래서 이걸 사용하는 클라이언트 코드를 바꿀 필요가 없다. 그래서 스프링이 이렇게 변환하는 메커니즘으로 동작한다.
  - ```java
    Member m1 = new Member("AAA", 10);
    Member m2 = new Member("AAA", 20);
    memberRepository.save(m1);
    memberRepository.save(m2);

    Optional<Member> findMemberAAAOpt = memberRepository.findOptionalByUsername("AAA");
    System.out.println("findMemberAAAOpt = " + findMemberAAAOpt);
    // 결과: org.springframework.dao.IncorrectResultSizeDataAccessException: query did not return a unique result: 2; nested exception is javax.persistence.NonUniqueResultException: query did not return a unique result: 2
    ```

### Slice

- totlaCount 는 가져오지 않고 다음 페이지는 가져온다.
- 예를 들어 명시적으로 limit 3개를 가져오려고 하면 Slice는 limit 3 + 1 = 4개로 하나 더 추가해서 가져오려고 한다.

### TotalCount

- 데이터가 많아질수록 성능에 문제가 생길 수 있으므로 쿼리를 직접 짜야하는 경우도 있다.

### 벌크 연산

- 영속성 컨텍스트가 flush, commit 을 하기 전에 벌크 연산이 실행되는 순간 바로 DB로 쿼리를 실행한다.
- 제일 깔끔한 로직은 벌크 연산 이후에 다른 쿼리 연산을 하지 않는 것이다.

### JPQL 실행문을 만났을 때

- 영속성 컨텍스트에 쿼리가 쌓여있다면 JPQL 실행문을 만나면 쌓여있는 쿼리가 JPQL 과 함께 flush 된다.

### 스프링 데이터 JPA 메서드 findByUsername 로 조회 했을 때, clear 하지 않아도 select 문이 실행되고 영속성 컨텍스트 캐시가 변경되지 않는 이유!

- findByUsername 메서드는, 스프링 데이터 JPA가 제공하는 메서드 이름으로 쿼리 호출 기능을 사용하신 것 같네요. <br />
  이 경우 스프링 데이터 JPA는 메서드 이름으로 JPQL을 만들어서 실행합니다. <br />
  결국 "select m from member m where m.username = ?" 같은 JPQL이 실행된 것이지요. <br />
  em.find()나, 지연로딩을 조회할 때는 1차 캐시에서 엔티티를 찾아오는 과정을 거칩니다. <br />
  반면에 JPA에서 JPQL은 항상 SQL로 번역되어서 DB를 통해 실행됩니다! <br />
  왜냐하면 em.find() 처럼 엔티티 하나를 찾아오는 것은 JPA 구현체 입장에서 key(식별자) 값이 명확하기 때문에 1차 캐시에서 찾기가 간단합니다. 그런데 JPQL은 식별자를 딱 찍어서 찾는 것도 아니고, 쿼리에 따라 1차 캐시보다 더 많은 데이터가 DB에 있을 수 도 있습니다. (예를 들어서 위 코드에서 벌크 연산 실행 직후에 누군가 member5의 데이터를 DB에 더 입력한다면 1차 캐시의 데이터 만으로는 그것을 다 알 수 없지요.) 그리고 기술적으로 JPQL을 분석해서 1차 캐시에서 조회하는 하도록 만드는 것도 매우 어렵습니다. <br />
  그래서 하이버네이트 구현체는 우선 JPQL을 실행하면 DB에서 데이터를 조회합니다. <br />
  단! 여기서 부터가 매우 중요한데요. (어드벤스 입니다 ㅎㅎ) <br />
  현재 1차 캐시에 다음과 같은 데이터가 있고, <br />
  ID: 1, name: memberA <br />
  DB에 다음과 같은 데이터가 있을 때 <br />
  ID: 1, name: memberB <br />
  예를 들어서 다음과 같은 JPQL을 실행하면 <br />
  select m from member m where m.id = 1 <br />
  우선 JPQL이기 때문에 DB에서 쿼리로 id:1, memberB 데이터를 조회합니다. <br />
  그런데 1차 캐시에 이미 id:1 이라고, 식별자가 충돌이 됩니다. <br />
  JPA는 영속성 컨텍스트의 동일성을 보장합니다. <br />
  따라서 DB의 결과 값을 버리고, 1차 캐시에 있는 결과값을 반환합니다. <br />
  이 부분에 대해서 더 자세한 내용은 JPA책 10.6.2 영속성 컨텍스트와 JPQL을 참고해주세요^^ <br />
  감사합니다.

### 지연 로딩 (Lazy Loading)

- Lazy로 설정된 Team은 null로 초기화할 수는 없어서 비어 있는 프록시라는 가짜 객체가 할당된다.
- Team의 필드를 get 시도를 할 때 DB로 쿼리를 실행해서 값을 채운다.
- 여기서 1 + N 문제가 발생한다.
- fetch join 을 하면 진짜 객체가 할당되고 프록시 객체는 할당되지 않는다.
- fetch join 은 객체 그래프에 연관된 객체를 포함해서 join 하고 select 절에 해당 객체들을 포함시킨다.

### 스프링 데이터 JPA 에서 fetch join JPQL을 작성하지 않고도 fetch join을 하는 방법

- @EntityGraph 애노테이션을 사용해서 (attributePaths = {"team"}) 이렇게 설정하면 team 을 fetch join 해온다.
- 내부적으로는 fetch join을 사용한다.
- @EntityGraph 로 JPQL 을 대체해서 fetch join 할 수 있다.
- @Query 에서 쿼리를 작성하고 @EntityGraph 를 통해서 fetch join 을 동시에 할 수 있다.

### 사용자 정의 리포지토리

- 복잡한 쿼리를 만들 때 사용
- 직접 DB에 연결해야할 때 사용
- JdbcTemplate 으로 구현할 때 사용
- QueryDSL 을 같이 사용할 때 사용
- 간단한 기능은 스프링 데이터 JPA, 복잡한 기능은 QueryDSL
- 규칙은 사용자 정의 인터페이스를 구현한 클래스에서 "JpaRespository 인터페이스를 구현한 클래스 파일명 + Impl" 은 규칙이다. 사용자 정의 인터페이스의 이름은 상관없다.

### 아키텍처가 커질수록 고려하면 좋은점

- 커맨드와 쿼리를 분리
- 핵심 비즈니스 로직과 아닌 것(화면에 맞춘 복잡한 쿼리 등)을 분리
- 이에 따라서 라이프사이클에 따라 뭘 변경해야 될 지가 달라지는 점
- 이런 부분들을 다각적으로 고민하면서 설계

### Auditing

- 실무에서 많이 사용

### 람다

- 인터페이스에서 메서드 하나면 람다로 바꿀 수 있다.

### AuditorAware

- Bean 으로 등록해야 한다.
- @CreatedBy, @LastModifiedBy 가 Bean 으로 등록된 이 인터페이스를 호출해서 자동으로 값을 가져간다.

### BaseTimeEntity 분리

- 등록일, 수정일을 사용하는 엔티티가 많으므로 공통으로 만들고 등록자, 수정자는 없는 엔티티도 있으므로 BaseTimeEntity 를 상속받는 방식으로 사용한다.

### 도메인 클래스 컨버터

- 엔티티를 파라미터로 받으면 조회용으로만 사용해야 한다. (트랜잭션이 없는 범위에서 엔티티를 조회했으므로 엔티티를 변경해도 DB에 반영되지 않는다.)

### Pageable

- 컨트롤러에서 파라미터가 Pageable 라면 PageRequest 객체를 생성해서 값을 채워서 인젝션을 해준다.
- ```http request
  http://localhost:8080/members?page=0&size=3&sort=id,desc&sort=username,desc
  ```
- @PageableDefault(size = 5, sort = "username") 애노테이션으로 설정 가능

### 엔티티

- 엔티티 내에서 DTO를 구현하지 않는게 좋다.
- DTO는 여러 엔티티에서 사용하기 때문에 내부에 엔티티를 사용해도 된다.
- DTO 내에서 엔티티가 필드로 사용되면 안된다. 생성자로 엔티티를 파라미터를 받아서 기존 필드를 초기화 할 때 사용한다.

### 스프링 데이터 JPA 내부동작

- 기본적으로 이미 구현체인 SimpleJpaRepository 리포지토리 계층에서 @Repository, @Transactional 를 사용한다.

### 새로운 엔티티 객체 구별법

- 엔티티 객체를 save 할 때 엔티티 객체의 식별자(id) 가 객체 타입(Long) 이라면 null 로 새로운 객체를 판단한다.
- long 기본 타입(프리미티브 타입) 일 경우는 0 으로 판단한다.

### merge

- 이미 DB에 있을 것이라고 가정하고 동작한다.
- DB에서 찾아서 없다면 새로 insert 한다.
- merge를 사용하지 않는 것이 좋다.
- save() 내부 로직에도 포함되어 있다.

### 나머지 기능들

- 다른 좋은 대안들이 많음
- 알고 있으면 편리하게 쓸 수 있음
- 직관적이지 않고 실용성 없이 복잡하므로 실무에서 잘 사용을 하지 않음
- 실무에서 Specifications (명세) 대신 QueryDSL 사용하는 것을 권장

- 프로젝션
  - 인터페이스 또는 클래스 프로젝션이 있다.
  - 클래스 프로젝션
    - 생성자의 파라미터 이름으로 매칭해서 프로젝션이 된다.
  - 중첩 구조
    - 첫번째 getUsername 은 하나만 가져오지만 두번째인 Team 은 최적화가 안되어 전부 select 된다.
    - 프로젝션 대상이 root 엔티티면 JPQL select 절 최적화가 가능하다.
    - 프로젝션 대상이 root 가 아니면 left outer join 을 하고 모든 필드를 select 해서 엔티티로 조회한 다음에 계산한다.
    - 실무에서 복잡한 쿼리를 해결하는데 한계가 있다.
    - 실무에서는 단순할 때만 사용하고, 조금만 복잡해지면 QueryDSL 사용하는 것을 권장
- 네이티브 쿼리

  - JPA를 사용하면 사용하지 않는게 좋다.
  - 최후의 수단으로 어쩔 수 없이 써야할 때만 사용한다. 실무에서 99% 로 사용하지 않는다. 주로 QueryDSL, JPQL 을 사용한다.
  - 엔티티를 select 절에 다 명시해야한다.
  - 쓰는 목적은 보통 엔티티 조회보다는 DTO로 변환할 때 사용한다.
  - 반환 타입 몇가지 지원이 안된다.
  - 반환 타입 Object[], Tuple 은 가독성이 좋지 않아서 DTO(스프링 데이터 인터페이스 Projections 지원)를 사용하는 것이 좋다.
  - Sort 파라미터를 통한 정렬이 정상 동작하지 않을 수 있다.
  - 애플리케이션 로딩 시점 문법 확인 불가
  - 동적 쿼리 불가
  - 네이티브 SQL로 DTO를 조회할 때는 JdbcTemplate 또는 myBatis 를 권장한다.
  - 페이징 네이티브 쿼리는 프로젝션에 선언한 이름과 SQL select 절의 컬럼명과 매칭시킨다.
  - 동적 페이징 네이티브는 JdbcTemplate 이나 myBatis, jooq 등 외부 라이브러리를 사용하는 것을 권장한다.

- 쿼리가 수백 줄 되면 보통 한번의 쿼리로 많은걸 한 3번 정도로 select 하는 경우로 바꾸면 몇십 줄로 줄일 수도 있다.
- 정산 시스템도 QueryDSL 로 개발했다.

### save()

- save() 파라미터로 넘어온 엔티티가 새로운 엔티티면 persist하고 아니라면 merge를 한다.
- persist는 1차 캐시에 저장한다.
- merge는 DB에서 조회해서 조회한 값을 파라미터로 넘어온 엔티티로 전부 교체하고 트랜잭션 끝날 때 DB에 반영이 된다.
- merge의 단점은 DB에서 select(조회)를 쿼리를 한다는 것이다.
- 왠만하면 merge는 사용하면 안된다.
- 값 변경은 트랜잭션이 끝날 때 엔티티의 값만 바꿔 놓으면 자동으로 바뀌는 변경감지(Dirty checking)를 사용해야 한다.
- merge는 비영속 상태 객체가 영속 상태 객체가 되어야 할 때 사용한다.

#### save() 메서드를 코드 레벨에서 분석

```java
@Id
@GeneratedValue
private Long id;
```

```java
@Test
public void save() {
    Item item = new Item();
    itemRepository.save(item);
}
```

```java
@Transactional
@Override
public <S extends T> S save(S entity) {

    Assert.notNull(entity, "Entity must not be null.");

    if (entityInformation.isNew(entity)) {
        em.persist(entity);
        return entity;
    } else {
        return em.merge(entity);
    }
}
```

- 여기서 item이 파라미터로 전달되면 isNew() 메서드를 호출에 엔티티를 파라미터로 넘겨주는데 식별자(PK)가 null 이라서 조건문이 true가 된다.
- persist로 엔티티가 넘어가서 메서드가 종료되면 "@GeneratedValue"에 의해서 JPA 안에서 만들어서 주입을 해준다.
- 여기서 문제는 Item 엔티티의 식별자(ID or PK)가 "@GeneratedValue"로 생성되지 않았을 때 생긴다.

```java
@Id
private String id;
```

```java
@Test
public void save() {
    Item item = new Item("A");
    itemRepository.save(item);
}
```

- 이렇게 코드를 변경하면 save 내부의 entityInformation.isNew(entity) 에서 PK가 존재("A")하기 때문에 조건문은 false가 된다.
  그래서 merge가 호출되는데 merge는 DB에 데이터가 있을 것이라고 가정하고 동작한다.
- merge가 DB에 select 쿼리를 날려서 없으면 새로운 데이터를 넣고 있다면 새로운 데이터로 전부 교체한다. <br/>
  merge는 select & update 비슷해서 사용하기 애매해서 이 메서드를 사용하는 경우는 거의 없다. <br/>
  사용하는 경우라면 특수한 상황인 detached 일 경우에 사용하지만 이런 경우는 거의 없다. <br/>
- 데이터를 변경할 때는 "Dirty checking"을 사용하고,
  저장할 때는 "persist"를 사용해야 한다.
- 기본적으로 "merge"는 사용하지 않아야 한다.

#### 식별자가 @GeneratedValue를 사용하지 않을 때 엔티티가 새로운 객체인지 구별하는 방법.

- 엔티티에서 Persistable 인터페이스를 구현하면 된다.
- public class Item implements Persistable<String>

```java
@EntityListeners(AuditingEntityListener.class)
@Id
private String id;

@CreatedDate // persist 가 되기 전에 호출
private LocalDateTime createdDate;

public Item(String id) {
    this.id = id;
}

@Override
public String getId() {
    return id;
}

@Override
public boolean isNew() {
    return createdDate == null;
}
```

- 오버라이딩한 getId()는 id를 기본적으로 리턴한다.
- 오버라이딩한 isNew()는 새로운 객체인지 판단하는 로직을 작성해야 한다.
- isNew()
  - @CreatedDate는 persist가 되기 전에 호출이 되어서 createdDate에 시간이 담긴다. (이 애노테이션을 사용하려면 엔티티에 @EntityListeners(AuditingEntityListener.class)를 추가해야 한다.)
  - 만약 이미 생성된 객체라면 isNew는 false가 되고 아직 생성되지 않은 객체라면 createdDate가 null이라서 true가 된다.
  - 엔티티 코드를 이렇게 수정하면 save() 메서드에서 isNew() 조건은 true가 되고 persist에 전달되어 정상적으로 종료되면 createdDate에 값이 들어간 상태로 반환된다.
