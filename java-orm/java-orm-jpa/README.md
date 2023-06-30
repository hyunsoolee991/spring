# 자바 ORM 표준 JPA 프로그래밍

```java
List<Member> result=em.createQuery("select m from Member as m",Member.class).getResultList();
```

위 코드는 JPQL이 객체를 대상으로 코드를 작성한다. <br />
JPA는 코드를 짤 때 테이블을 대상으로 코드를 작성하지 않고 <br />
엔티티 객체를 대상으로 코드를 작성한다. <br />
JPQL로 작성한 쿼리는 특정 DB에 종속되지 않은 문법으로 객체를 대상으로 쿼리를 수행한다. <br />

```java
 List<Member> result=em.createQuery("select m from Member as m",Member.class)
        .setFirstResult(5)
        .setMaxResults(8)
        .getResultList();

        for(Member member:result){
        System.out.println("member.getName() = "+member.getName());
        }
```

JPQL은 객체를 대상으로 하는 객체지향 쿼리이다.
설정한 DB의 Dialect(방언)에 맞게 번역하여 동작한다.
대부분의 ANSI SQL의 쿼리를 지원해준다.

### flush()

em.flush()를 호출하면 1차 캐시에 있는 값들이 사라지지는 않고
쓰기 지연 SQL 저장소에 있는 쿼리들만 DB에 반영된다.
즉, 영속성 컨텍스트를 비우지 않는다.

### JPQL 쿼리 실행 시 flush가 자동으로 호출되는 이유

```java
em.persist(memberA);
em.persist(memberB);
em.persist(memberC);

// 중간에 JPQL 실행
TypedQuery<Member> query=em.createQuery("select m from Member m",Member.class);
List<Member> members=query.getResultList();
```

여기서 persis를 한 경우에는 아직 DB에 반영되지 않았고
그 다음 JPQL이 DB에서 데이터를 조회하려고 하는데 JPA의 persist에서 아직 DB에 반영되지 않았으니,
검색을 하기 위해서 쓰기 지연 SQL 저장소에 쿼리들을 flush 수행하여 DB에 반영한다.
즉, 쿼리가 실행되면 flush가 실행된다.
