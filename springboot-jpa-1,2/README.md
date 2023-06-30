# 스프링 부트와 JPA 활용 1, 2

### 단축키

- 선택 리팩토링 : Control + T
- 다음 에러로 리동 : F2
- 선언한 곳으로 이동 : Command + B

### Repository

- Repository는 DAO와 비슷한 개념이라고 보면된다.

### 식별자가 같은 엔티티

- 같은 트랜잭션이면 영속성 컨텍스트도 똑같고 식별자(ID)가 동일하면 같은 엔티티로 식별한다.
- 1차 캐시에서 기존에 있던 엔티티를 반환한다.

```sql
@Test
    @Transactional
    @Rollback(value = false)
    public void testMember() throws Exception {

        // given
        Member member = new Member();
        member.setUsername("memberA");

        // when
        Long saveId = memberRepository.save(member);
        Member findMember = memberRepository.find(saveId);

        // then
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());

        System.out.println("member = " + member);
        System.out.println("findMember = " + findMember);

        System.out.println("member.getClass() = " + member.getClass());
        System.out.println("findMember.getClass() = " + findMember.getClass());
    }
```

### 도메인 모델과 테이블 설계

회원은 여러 상품을 주문할 수 있다.
회원 1 주문 N -> 일대다 관계

회원이 한 번 주문할 때 여러개 상품을 주문할 수 있다.
상품은 여러 주문에 담길 수 있다.
주문 N 상품 N -> 다대다 관계 (주문상품 테이블을 따로 생성)

### 다대다 관계 사용을 자제해야 할 이유

- 중간 테이블에는 매핑정보만 들어가고 추가 데이터를 넣는 것이 불가능하다.
- 중간 테이블이 숨겨져 있기 때문에 쿼리가 예상하지 못하는 형태로 나간다.
- 실무 비즈니스는 복잡해서 ManyToMany로 풀 수있는게 거의 없다고 보면 된다.

### 연관관계의 주인은 누구로?

외래 키가 있는 곳을 연관관계의 주인으로 정해라.
연관관계의 주인은 단순히 외래 키를 누가 관리하냐의 문제이지 비즈니스상 우위에 있다고 주인으로 정하면
안된다.. 예를 들어서 자동차와 바퀴가 있으면, 일대다 관계에서 항상 다쪽에 외래 키가 있으므로 외래 키가 있는 바퀴를 연관관계의 주인으로 정하면 된다.
물론 자동차를 연관관계의 주인으로 정하는 것이 불가능 한 것은 아니지만, 자동차를 연관관계의 주인으로 정하면 자동차가 관리하지 않는 바퀴 테이블의 외래 키 값이 업데이트 되므로 관리와 유지보수가 어렵고,
추가적으로 별도의 업데이트 쿼리가 발생하는 성능 문제도 있 다. 자세한 내용은 JPA 기본편을 참고하자.

### Setter는 가급적 사용하지 말자!

이론적으로 Getter, Setter 모두 제공하지 않고, 꼭 필요한 별도의 메서드를 제공하는게 가장 이상적 이다.
하지만 실무에서 엔티티의 데이터는 조회할 일이 너무 많으므로, Getter의 경우 모두 열어두는 것이 편리하다.
Getter는 아무리 호출해도 호출 하는 것 만으로 어떤 일이 발생하지는 않는다. 하지만 Setter는 문제가 다르다. Setter를 호출하면 데이터가 변한다.
Setter를 막 열어두면 가까운 미래에 엔티티에가 도대 체 왜 변경되는지 추적하기 점점 힘들어진다.
그래서 엔티티를 변경할 때는 Setter 대신에 변경 지점이 명확 하도록 변경을 위한 비즈니스 메서드를 별도로 제공해야 한다.

### 일대일 관계

- 외래키는 어떤 테이블에 두든 상관없다. 하지만 주로 액세스가 많이 되는 곳에 외래키를 둔다.

### 외래키 꼭 필요하나?

- 시스템 마다 다르다.
- 실시간 트래픽이 중요하고 정합성 보다 서비스가 잘되는게 중요하면 외래키 없이 인덱스만 잘 잡아줘도 된다.
- 돈과 관련되고 서비스가 중요하다면 외래키를 사용을 고려해야 한다.

### @ManyToMany 사용하면 안되는 이유

@ManyToMany 를 사용하지 말자
@ManyToMany 는 편리한 것 같지만, 중간 테이블( CATEGORY_ITEM )에 컬럼을 추가할 수 없고,
세밀하게 쿼 리를 실행하기 어렵기 때문에 실무에서 사용하기에는 한계가 있다.
중간 엔티티( CategoryItem 를 만들고 @ManyToOne , @OneToMany 로 매핑해서 사용하자.
정리하면 대다대 매핑을 일대다, 다대일 매핑으로 풀어 내서 사용하자.

### 값 타입은 변경 불가능하게 설계!

값 타입은 변경 불가능하게 설계해야 한다.
@Setter 를 제거하고, 생성자에서 값을 모두 초기화해서 변경 불가능한 클래스를 만들자. JPA 스펙상 엔티
티나 임베디드 타입( @Embeddable )은 자바 기본 생성자(default constructor)를 public 또는 protected 로 설정해야 한다. public 으로 두는 것 보다는 protected 로 설정하는 것이 그나마 더 안전 하다.
JPA가 이런 제약을 두는 이유는 JPA 구현 라이브러리가 객체를 생성할 때 리플랙션 같은 기술을 사용할 수 있도록 지원해야 하기 때문이다.

### fetch EAGER를 사용한다면?

- 주인 테이블 Order를 조회할 때, 쿼리 한 번에 Member를 join해서 가져온다.
- N + 1 문제가 발생한다.
  - JPQL에서 Order 테이블만 조회하면 Order 테이블의 결과만 나오는데 결과가 100이라면 Member를 조회하기 위해서 각 Order에 대한 단건 쿼리가 실행된다.

### 엔티티에는 가급적 Setter를 사용하지 말자

- Setter가 모두 열려있다. 변경 포인트가 너무 많아서, 유지보수가 어렵다. 나중에 리펙토링으로 Setter 제거

### 모든 연관관계는 지연로딩으로 설정!

- 즉시로딩( EAGER )은 예측이 어렵고, 어떤 SQL이 실행될지 추적하기 어렵다. 특히 JPQL을 실행할 때 N + 1 문제가 자주 발생한다.
- 실무에서 모든 연관관계는 지연로딩( LAZY )으로 설정해야 한다.
- 연관된 엔티티를 함께 DB에서 조회해야 하면, fetch join 또는 엔티티 그래프 기능을 사용한다. @XToOne(OneToOne, ManyToOne) 관계는 기본이 즉시로딩이므로 직접 지연로딩으로 설정해야 한 다.

### 컬렉션은 필드에서 초기화 하자.

- 컬렉션은 필드에서 바로 초기화 하는 것이 안전하다.
- null 문제에서 안전하다.
- 하이버네이트는 엔티티를 영속화 할 때, 컬랙션을 감싸서 하이버네이트가 제공하는 내장 컬렉션으로 변경한 다. 만약 getOrders() 처럼 임의의 메서드에서 컬력션을 잘못 생성하면 하이버네이트 내부 메커니즘에 문 제가 발생할 수 있다. 따라서 필드레벨에서 생성하는 것이 가장 안전하고, 코드도 간결하다.

```java
Member member = new Member();
System.out.println(member.getOrders().getClass());
em.persist(member);
System.out.println(member.getOrders().getClass());

//출력 결과
class java.util.ArrayList
class org.hibernate.collection.internal.PersistentBag
```

### 테이블, 컬럼명 생성 전략

- 스프링 부트에서 하이버네이트 기본 매핑 전략을 변경해서 실제 테이블 필드명은 다름
- https://docs.spring.io/spring-boot/docs/2.1.3.RELEASE/reference/htmlsingle/#howto-configure-hibernate-naming-strategy
- http://docs.jboss.org/hibernate/orm/5.4/userguide/html_single/Hibernate_User_Guide.html#naming

- 하이버네이트 기존 구현: 엔티티의 필드명을 그대로 테이블 명으로 사용 ( SpringPhysicalNamingStrategy )
- 스프링 부트 신규 설정 (엔티티(필드) 테이블(컬럼))
  1. 카멜 케이스 언더스코어(memberPoint member_point)
  2. .(점) \_(언더스코어)
  3. 대문자 소문자

적용 2 단계

1. 논리명 생성: <br />
   - 명시적으로 컬럼, 테이블명을 직접 적지 않으면 ImplicitNamingStrategy 사용
   - spring.jpa.hibernate.naming.implicit-strategy : 테이블이나, 컬럼명을 명시하지 않을 때 논리명
     적용,
2. 물리명 적용: <br />
   - spring.jpa.hibernate.naming.physical-strategy : 모든 논리명에 적용됨, 실제 테이블에 적용 (username usernm 등으로 회사 룰로 바꿀 수 있음)
   - **스프링 부트 기본 설정** <br/>
     애플리케이션 구현 준비 구현 요구사항 <br />
     ```xml
     spring.jpa.hibernate.naming.implicit-strategy:
     org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy
     spring.jpa.hibernate.naming.physical-strategy:
     org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy
     ```

### persist

persist를 하면 영속성 컨텍스트에는 엔티티의 기본키를 컨텍스트의 키로 지정한다.
DB에 접속하지 않은 상태에 영속성 컨텍스트에 키와 값을 넣어야하는데 자동적으로 생성한 키가 영속성 컨텍스트에 들어간다.

### 애노테이션

- @Transactional(readOnly = true)
  - true로 설정하면 성능이 좀 더 좋다. 읽기만 하는 로직이라면 사용하는 것이 좋다.
  - 이 애노테이션이 붙은 메소드가 종료되는 시점에 자동으로 commit 된다.
- @Autowired:
  - 생성자가 1개라면 애노테이션을 명시하지 않아도 자동으로 주입해준다.
    ```java
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
    ```
- @RequiredArgsConstructor:
  - 생성자를 만들지 않아도 생성자가 필요한 필드(final 키워드를 포함한)만 생성자로 만들어준다.
- @SpringBootTest : 명시하지 않으면 @Autowired 는 동작 하지 않는다.
- @RestController : @Controller, @ResponseBody 를 합친 애노테이션

### validateDuplicateMember 메서드

- 멀티 쓰레드나 동시에 같은 이름으로 가입하여 충돌날 수도 있기 때문에 DB에 유저네임을 유니크 제약조건으로 설정하는식으로 실무에서는 이것보다 더 안전하게 해야한다.

### 테스트 환경에서 DB 설정

- 테스트 경로에 스프링 부트 설정 (application.yml) 파일을 만들고 커넥션 정보를 입력하지 않으면 스프링이 h2 메모리 db를 기본으로 사용한다.

### 스프링 부트 ddl-auto 기본 값

- ddl-auto: create-drop 으로 기본 값이 설정되어 있다.

### 데이터(필드)를 가지고 있는 쪽에 비즈니스 메서드가 있는 것이 좋다.

- Item 클래스에 stockQuantity 필드(데이터)와 addStock 메서드가 있는 것 응집력이 있고 관리하는 것이 좋다.

  ```java
    private int stockQuantity;

    ...

    //== 비즈니스 로직 ==//
    /**
     * stock(재고) 증가
     */
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }
  ```

- 'stockQuantity' 필드의 값 변경이 필요하다면 setter 메서드가 아닌 핵심 비즈니스 메서드(addStock)를 통해서 변경해야 한다.
- 다른 곳에서 setter를 통해서 해당 필드를 수정하는 것이 아니라 해당 필드를 수정하는 핵심 비즈니스 메서드가 해당 필드의 클래스 내에 있는 것이 좋다.

### cascade = CascadeType.X

- 이 속성을 사용하는 클래스 내부에서 해당 속성의 영향을 받는 의존 객체는 이 클래스를 포함해서 또 다른 다른 클래스를 가리키고 있다면 캐스케이드를 사용하면 안된다.
- 이 속성에 영향을 받는 의존 객체의 오너가 한 개일 때만 사용하는 것이 좋다.

### 도메인 모델 패턴

- 주문 서비스의 주문과 주문 취소 메서드를 보면 비즈니스 로직 대부분이 엔티티에 있다. 서비스 계층 은 단순히 엔티티에 필요한 요청을 위임하는 역할을 한다. 이처럼 엔티티가 비즈니스 로직을 가지고 객체 지 향의 특성을 적극 활용하는 것을 **도메인 모델 패턴**(http://martinfowler.com/eaaCatalog/ domainModel.html)이라 한다. 반대로 엔티티에는 비즈니스 로직이 거의 없고 서비스 계층에서 대부분 의 비즈니스 로직을 처리하는 것을 트랜잭션 스크립트 패턴(http://martinfowler.com/eaaCatalog/ transactionScript.html)이라 한다.
- JPA를 사용하면 도메인 모델 패턴으로 개발을 더 많이 하게 된다.
- 유지보수가 쉬운 패턴을 상황에 따라 선택하면 된다.

### 테스트

- 테스트를 통합적으로 하는 것도 중요한데, 메서드 별로 단위 테스트하는 것도 중요하다.
- 통합적인 테스트보다 DB 없이 단위적으로 하는게 성능도 더 빠르고 더 좋은 테스트일 확률이 높다.

### 컨트롤러에서 엔티티 업데이트

- 만약 업데이트할 데이터(필드)가 많아서 전달할 파라미터가 많다면 서비스 계층에 DTO를 따로 만드는 것이 좋다.

### 커맨드(사용자 요청)

- 컨트롤러 레벨에서는 식별자만 넘기고 핵심 비즈니스 로직에서 엔티티를 찾는 로직을 수행하는 것을 권장한다.
- 조회를 할 경우는 상관이 없지만, 핵심 비즈니스 로직이 있는 경우에는 식별자만 넘겨서 한 트랜잭션 안에서 영속성 컨텍스트를 관리하는 것이 좋다.

### API 요청 스펙에 맞춰서 별도의 DTO를 만들자!

- 엔티티를 API 스펙으로 사용하지 않아야한다.
- 엔티티는 여러 곳에서 사용할 확률이 높아서 엔티티 대신 별도의 DTO로 만드는 것이 좋다.
- API를 만들 때는 엔티티를 파라미터로 받으면 안된다.
- 엔티티를 외부에 노출하면 안된다.

### v2 API를 사용하면!

- 엔티티와 프레젠테이션 계층을 분리할 수 있다.
- 엔티티와 API 스펙을 명확하게 분리할 수 있다.
- 엔티티가 변경되어도 API 스펙이 변하지 않는 장점이 있다.
- 실무에서는 엔티티를 외부 노출하거나 파라미터로 받는 것은 안하는게 좋다.
- 항상 DTO 같은 객체를 사용하는 것을 권장한다.

### 트랜잭션이 종료될 때!

- @Transactional 이 붙은 메서드가 종료되면 스프링 AOP가 동작하면서 트랜잭션 애노테이션에 의해서 트랜잭션에 관련된 AOP가 끝나는 시점에 트랜잭션 커밋이 된다.
  그 때 JPA가 영속성 컨텍스트를 flush, commit 하고 DB 트랜잭션 commit 을 한다.

### 커맨드와 쿼리를 분리

- Member를 update 할 때, update(커맨드)와 Member를 리턴한다면
  id를 가지고 member를 조회(쿼리)를 하고 그 Member를 리턴하는 경우라서 때문에 동시에 일어나기 때문에 커맨드와 쿼리를 분리하기 위해서 Member를 리턴하지 않았다.

### update 후 다시 member를 조회할 때 쿼리가 실행되지 않는 이유

- 해당 부분은 OSIV 와 관련이 있다.
- 트랜잭션이 끝나도 영속성 컨텍스트가 살아있기 때문에 update 후에 findOne 을 호출하면 1차캐시에 있는 해당 엔티티를 불러온다.

### DTO를 사용하면 !?

- API 스펙하고 DTO가 1:1이 된다!
- 엔티티를 노출하면 전부 노출되기 때문에 클라이언트에서 구분이 안될 수 있다!
- 꼭 필요한 데이터만 노출해야 한다!
- 유지보수가 더 좋다!

### fetch 타입이 지연로딩(LAZY) 경우 동작 방식

- 지연로딩인 경우 진짜 인스턴스를 할당하지 않고 DB에 연결하기 전에 하이버네이트가 바이트버디 프록시 라이브러리를 상속 받아서 진짜 객체 상속 받아서 할당한다.
- 진짜가 아닌 프록시 객체를 할당하고 객체의 특정 필드를 꺼내거나 설정하면 그 때 DB에 SQL를 실행하고 DB에서 값을 가져와서 할당한다.
- JSON이 반복문을 돌리는데 진짜 객체가 아닌 프록시 객체라서 JSON으로 변환하지 못해서 에러가 발생한다.
- 프록시 객체인 경우 JSON으로 변환하는 과정을 무시하는 방법이 있다.
- 지연로딩은 기본적으로 영속성 컨텍스트와 통신한다.

### V1 Orders 가져오기

```java
@GetMapping("/api/v1/simple-orders")
public List<Order> ordersV1() {

    List<Order> all = orderRepository.findAllByString(new OrderSearch());

    for (Order order : all) {

        order.getMember().getName();

        order.getDelivery().getAddress();
    }

    return all;
}
```

- order.getMember() 를 호출하면 프록시 객체를 반환하고 아직 DB로 쿼리를 실행하지 않는다.
- getName() 시점에 Lazy가 강제 초기화가 되어 Member로 쿼리를 실행하고 JPA 하이버네이트가 데이터를 다 가져온다.

### V1, V2의 문제점

- Lazy loading 으로 인한 쿼리가 너무 많이 실행되는 문제

### Lazy 초기화란?

- order.getMember().getName() // Lazy 초기화
  - 영속성 컨텍스트가 멤버 id를 가지고 영속성 컨텍스트에서 찾아오는데 없다면 DB로 쿼리를 실행한다.

### V2의 orders 조회는 1 + N(또는 N + 1) 문제 발생!

- 검색 1번 + orders N번 (2번이라고 가정)
  - orders 2번 == member 2번 + delivery 2번
    - 1 + N + N 이 최종적으로 쿼리가 실행된다.
    - 최악의 경우가 1 + N + N 이 된다.
    - 지연로딩은 기본적으로 영속성 컨텍스트와 통신하기 때문에 같은 값이 이미 영속성 컨텍스트에 있다면 쿼리가 실행되지 않는다.

### V2의 orders 조회, 1 + N(또는 N + 1) 의 해결을 EAGER Loading 으로 하려고 한다면?

- 처음에 검색 1번을 하고 EAGER 로 되어 있으면 예측이 힘들게 관련된 엔티티를 join해서 쿼리를 실행한다.
- 그래서 Lazy Loading 으로 가져오는게 낫다.

### fetch join 실무에서 사용하려면 ?

- 실무에서 fetch join을 사용하려면 이해를 명확하게 하는게 좋다!

### V3 orders 조회에서 달라진점?

- fetch join 으로 미리 Member와 Delivery를 한 번에 가져왔으므로 Lazy Loading 이 실행되지 않는다!

### SimpleOrderQueryDto 를 따로 만든 이유?

- 리파지토리가 컨트롤러를 가리키는 이상한 의존관계를 가질 수 있어서 단방향으로 설계하기 위해서 따로 만들었다.
- 의존관계는 안으로 들어오거나 헥사곤 아키텍쳐 처럼 인터페이스 형태로 하지 않는 이상 가급적 컨트롤러 -> 서비스 -> 리파지토리 로 한 방향으로 흐르는게 좋다.

### JPQL 쿼리 매핑은 기본적으로 DTO는 안된다!

- ```java
  em.createQuery(
        "select o from Order o " +
        "join o.member m " +
        "join o.delivery d", OrderSimpleQueryDto.class
  ).getResultList();
  ```

- 기본적으로 엔티티나 벨류 오브젝트(임베디드)만 반환할 수 있다.
- 매핑을 하려면 new Operation 을 사용해야 한다!
- new Operation 에서 엔티티를 넣어버리면 JPQL이 기본적으로 식별자를 전달하기 때문에 모든 필드를 파라미터로 전달해야 한다!
- new Operation 에서 임베디드 타입은 엔티티와 다르게 잘 동작한다!

### V3, V4 Orders 조회의 차이

- V3 <br />

  ```sql
    select
        order0_.order_id as order_id1_6_0_,
        member1_.member_id as member_i1_4_1_,
        delivery2_.delivery_id as delivery1_2_2_,
        order0_.delivery_id as delivery4_6_0_,
        order0_.member_id as member_i5_6_0_,
        order0_.order_date as order_da2_6_0_,
        order0_.status as status3_6_0_,
        member1_.city as city2_4_1_,
        member1_.street as street3_4_1_,
        member1_.zipcode as zipcode4_4_1_,
        member1_.name as name5_4_1_,
        delivery2_.city as city2_2_2_,
        delivery2_.street as street3_2_2_,
        delivery2_.zipcode as zipcode4_2_2_,
        delivery2_.status as status5_2_2_
    from
        orders order0_
    inner join
        member member1_
            on order0_.member_id=member1_.member_id
    inner join
        delivery delivery2_
            on order0_.delivery_id=delivery2_.delivery_id
  ```

- V4 <br />

  ```sql
     select
        order0_.order_id as col_0_0_,
        member1_.name as col_1_0_,
        order0_.order_date as col_2_0_,
        order0_.status as col_3_0_,
        delivery2_.city as col_4_0_,
        delivery2_.street as col_4_1_,
        delivery2_.zipcode as col_4_2_
    from
        orders order0_
    inner join
        member member1_
            on order0_.member_id=member1_.member_id
    inner join
        delivery delivery2_
            on order0_.delivery_id=delivery2_.delivery_id
  ```

- V3는 모든 필드를 가져오지만 V4는 원하는 필드만 Select 해서 애플리케이션 네트워크 용량을 최적화했다. (생각보다 미비)
- V3는 외부의 영향을 안받고 내부에서 모든걸 처리해서 재사용성이 높고, V4는 해당 쿼리를 사용하는 DTO에만 사용할 수 있어서 재사용을 할 수 없다.
- V3는 엔티티를 조회해서 비즈니스 로직에서 데이터 변경이 가능하지만, V4는 이미 DTO로 조회했기 때문에 데이터를 변경할 수 없다.
- 이 둘의 성능 차이는 거의 없다.
- select 절에서는 성능 차이는 크게 영향을 주지 않고, 성능 문제는 join, where 등 에서 많이 발생한다.
  하지만 select 절의 결과가 많다면 고민을 해봐야 한다.

### 리파지토리의 재사용성

- 리파지토리는 엔티티의 객체 그래프를 조회할 경우에 사용한다!
- findOrderDtos 처럼 API 스펙에 맞춰서 리포지토리를 만들면 안된다!
- findOrderDtos 는 물리적으로는 계층이 나눠져있지만, 논리적으로 계층이 깨져있다.
- 리파지토리에서 엔티티를 조회하거나 findAllWithMemberDelivery 처럼 fetch join으로 가져오는 경우는 엔티티의 순수성이 유지된다.
- findAllWithMemberDelivery 리파지토리로 화면을 의존하고 있다. 그래서 API 스펙이 바뀌면 해당 리파지토리를 변경해야 한다!

### OrderSimpleQueryRepository 를 따로 만든 이유?

- 리파지토리는 순수한 엔티티를 조회하는데 사용하는게 좋다. (재사용성 및 성능 최적화을 위해서)
- 특정 API 스펙에 의존적인 리파지토리는 기본 리파지토리와 분리해서 관리한다. (유지보수을 위해서)

### Hibernate5Module

- 이 모듈을 기본설정으로 사용하면 지연로딩으로 프록시가 정상적으로 초기화된 객체만 API로 반환한다.

### DTO 안에 엔티티가 있으면 안된다!

- DTO와 엔티티의 의존관계는 없어야 한다!
- DTO 안에 엔티티가 있다면 그 엔티티 또한 DTO로 만들어야 한다!

### distinct 키워드

- DB에서 distinct 키워드는 한 레코드의 칼럼 전부와 동일한 레코드가 있으면 중복이 제거된다.
- JPA의 JPQL에서 distinct 키워드가 있는 객체에서 같은 식별자(id)가 있으면 중복을 자동으로 제거해준다.

### 일대다의 치명적인 단점

- 페이징이 불가능하다.
- 일대다를 fetch join 하는 순간 페이징 쿼리(limit 또는 offset 등) 없이 실행된다.
- DB에서 페이징을 하지 않고 하이버네이트가 메모리에서 페이징 처리를 한다. 만약 결과가 1만개라면 메모리에서 페이징 처리를 해서 out of memory 와 같은 에러가 발생할 수 있다.
  - 하이버네이트가 이렇게 동작하는 이유는 일대다 페치 조인을 하는 정확한 예상 결과를 얻을 수 없다.
  - 2022-12-11 03:22:22.893 WARN 1087 --- [nio-8080-exec-5] o.h.h.internal.ast.QueryTranslatorImpl : HHH000104: firstResult/maxResults specified with collection fetch; applying in memory!
  - 일대다 페치 조인에서는 페이징을 하면 안된다.
- 컬렉션 페치 조인은 1개만 사용해야 한다. 컬렉 션 둘 이상에 페치 조인을 사용하면 안된다. 데이터가 부정합하게 조회될 수 있다.

### hibernate.default_batch_fetch_size

- orderRepository.findAllWithMemberDelivery(offset, limit) 에서 반환된 각각의 객체를 반복문으로 컬렉션을 조회하면
  모든 객체의 컬렉션을 쿼리에 in 키워드에 반환된 각각의 객체의 식별자를 size의 개수만큼 한 번에 쿼리를 실행한다.
- size는 쿼리의 in 키워드에 들어갈 사이즈를 말한다.
- size가 10 일 때, 반환된 객체가 10개이고 반복문이 객체의 사이즈만큼 10번 반복해서 컬렉션을 조회한다면 반복문 첫번째에서 in 키워드에 모든 객체의 식별자로 쿼리를 실행하게 된다.
- size가 10 이고, 반환된 객체가 100개면 in 키워드에 식별자가 10개만 들어가서 쿼리가 실행된다.
- N + 1 (1 + N) 문제를 어느정도 해결할 수 있다.
- 글로벌 설정을 하는 경우에 이 속성을 사용하고 개별적으로 사용하고 싶으면 컬렉션 필드에 @BatchSize 애노테이션을 붙이면 된다.
  컬렉션 필드가 아닌 객체가 있는 클래스에 애노테이션을 붙여야 한다.
- 쿼리 실행 수가 N + 1 (1 + N) 에서 1 + 1 로 최적화 된다.
- 사이즈는 100 ~ 1000 이 좋고 DB와 WAS가 부하를 감당할 수 있으면 사이즈를 높게 줘도 괜찮다. 써보면서 상황에 따라 늘려가면 된다. 사이즈에 따른 동일한 데이터 처리의 메모리 사용량은 동일하다. 성능은 1000이 가장 좋다.

### orders V3 로 fetch join 할 때 문제점과 V3.1 과 무슨 차이가 있을까?

- 쿼리를 한 번만 실행하는 장점이 있지만 중복 데이터가 많아서 데이터 전송량이 많아진다.
- V3.1 로 조회하면 V3 보다 쿼리 실행은 많지만 정규화된 테이블로 반환되어 중복이 없는 테이블 결과가 나온다.
- V3.1은 페이징이 가능하다.

### 데이터에 따른 쿼리 명령

- 데이터가 별로 없다면 fetch join으로 하는게 좋다.
- 예를들어 데이터가 1000개를 실행해야 한다면 V3.1 이 더 성능이 좋을 수 있다.
- V3 과 V3.1 은 트레이드 오프가 있다.

### V3.1 findAllWithMemberDelivery

- xToOne 관계의 객체는 fetch join을 사용하지 않으면 in 키워드로 변경되어 쿼리가 실행된다.
  하지만 fetch join 를 쓰는 경우가 in 키워드를 사용하는 경우보다 쿼리 실행을 더 적게하므로 xToOne 객체는 fetch join 을 하는게 좋다.

### JPQL에서 new Operation

- new Operation 으로 생성자에 파라미터는 컬렉션을 넣을 수 없다.
- 데이터를 플랫하게 한 줄 밖에 넣지 못한다.

### V4 의 문제점은?

- V4도 1 + N 문제가 발생한다.
  - 조회 1, 컬렉션 N번 실행

### V5 에서 달라진점?

- 쿼리에 in 키워드가 추가되어 총 쿼리 실행은 2번으로 최적화가 된다.
  - 루트 1, 컬렉션 1

### V6 에서 달라진점?

- 쿼리는 한번이지만 join 으로 인해서 DB가 애플리케이션에 중복 데이터가 전달되므로 V5 보다 더 느릴 수도 있다.
- 애플리케이션에서 추가 작업이 많다.
- Order 를 기준으로 중복 데이터를 가져와서 페이징 불가능
- OrderItem 을 기준으로 하면 페이징 가능

### 정리

- 엔티티는 직접 캐싱을 하면 안된다.
  - 영속성 컨텍스트가 관리하고 있는데 캐시가 있으면 캐시가 안지워진다.
  - 캐시는 DTO로 변환해서 DTO를 캐싱해야 한다.
  - 엔티티를 캐시하는 방법은 하이버네이트 2차 캐시가 있는데 실무에서 적용하기가 까다롭다.
  - 레디스나 로컬 메모리로 DTO를 캐시한다.

### OrderService

- OrderService : 핵심 비즈니스 로직
- OrderQueryService : 화면이나 API에 맞춘 서비스 (주로 읽기 전용 트랜잭션 사용)
- 애플리케이션이 커지면 아키텍처에 따라서 컨트롤러, 애플리케이션 서비스, 도메인 서비스, 리파지토리로 계층을 한 단계 더 사용하는 경우도 있다.

### OSIV

- JPA가 DB 커넥션
  - DB 트랙잭션을 시작할 때, 영속성 컨텍스트가 DB 커넥션을 가져온다.
    - 서비스 계층에서 트랜잭션을 시작할 때 DB 커넥션을 가져온다.
    - spring.jpa.open-in-view 가 true 면 서비스 계층의 트랜잭션이 종료되도 DB 커넥션이 반환되지 않고 영속성 컨텍스트도 제거 되지 않는다.
      - API인 경우는 API가 유저한테 반환되기 전까지 DB 커넥션이 반환되지 않고 영속성 컨텍스트도 존재한다.
      - 뷰 템플릿인 경우는 화면이 렌더링 전까지 DB 커넥션이 반환되지 않고 영속성 컨텍스트도 존재한다.
    - spring.jpa.open-in-view 가 false 면 트랜잭션이 종료될 때 영속성 컨텍스트가 클리어되고 DB 커넥션을 반환한다. 커넥션 리소스를 낭비하지 않는다.
    - 영속성 컨텍스트는 트랜잭션이 시작될 때 생성된다.
- 고객 서비스의 트래픽이 많은 실시간 API는 OSIV 를 끄고, ADMIN 처럼 커넥션이 많지 않은 곳에서는 OSIV 를 켠다.

### ORDER_ITEM

- 여기서 ORDERPRICE 칼럼은 할인 등을 적용할 수 있어서 추가했다.

### Simple Orders V2, V3 fetch join 유무에 따른 Select Query 결과

- V2는 "N + 1" 문제로 인해서 쿼리가 5번 실행된다.
- V3는 "fetch join"을 사용해서 1번 실행된다.

#### V2

```sql
    select
        order0_.order_id as order_id1_6_,
        order0_.delivery_id as delivery4_6_,
        order0_.member_id as member_i5_6_,
        order0_.order_date as order_da2_6_,
        order0_.status as status3_6_
    from
        orders order0_
    inner join
        member member1_
            on order0_.member_id=member1_.member_id limit ?


    select
        member0_.member_id as member_i1_4_0_,
        member0_.city as city2_4_0_,
        member0_.street as street3_4_0_,
        member0_.zipcode as zipcode4_4_0_,
        member0_.name as name5_4_0_
    from
        member member0_
    where
        member0_.member_id=?


    select
        delivery0_.delivery_id as delivery1_2_0_,
        delivery0_.city as city2_2_0_,
        delivery0_.street as street3_2_0_,
        delivery0_.zipcode as zipcode4_2_0_,
        delivery0_.status as status5_2_0_
    from
        delivery delivery0_
    where
        delivery0_.delivery_id=?


    select
        member0_.member_id as member_i1_4_0_,
        member0_.city as city2_4_0_,
        member0_.street as street3_4_0_,
        member0_.zipcode as zipcode4_4_0_,
        member0_.name as name5_4_0_
    from
        member member0_
    where
        member0_.member_id=?


    select
        delivery0_.delivery_id as delivery1_2_0_,
        delivery0_.city as city2_2_0_,
        delivery0_.street as street3_2_0_,
        delivery0_.zipcode as zipcode4_2_0_,
        delivery0_.status as status5_2_0_
    from
        delivery delivery0_
    where
        delivery0_.delivery_id=?
```

#### V3

```sql
    select
        order0_.order_id as order_id1_6_0_,
        member1_.member_id as member_i1_4_1_,
        delivery2_.delivery_id as delivery1_2_2_,
        order0_.delivery_id as delivery4_6_0_,
        order0_.member_id as member_i5_6_0_,
        order0_.order_date as order_da2_6_0_,
        order0_.status as status3_6_0_,
        member1_.city as city2_4_1_,
        member1_.street as street3_4_1_,
        member1_.zipcode as zipcode4_4_1_,
        member1_.name as name5_4_1_,
        delivery2_.city as city2_2_2_,
        delivery2_.street as street3_2_2_,
        delivery2_.zipcode as zipcode4_2_2_,
        delivery2_.status as status5_2_2_
    from
        orders order0_
    inner join
        member member1_
            on order0_.member_id=member1_.member_id
    inner join
        delivery delivery2_
            on order0_.delivery_id=delivery2_.delivery_id
```
