package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.NamedEntityGraph;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    // 같은 트랜잭션이면 같은 엔티티 매니저로 동작하게 된다.
    // memberRepository, teamRepository 모두 같은 엔티티 매니저를 사용한다.
    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember() {

        System.out.println("memberRepository.getClass() = " + memberRepository.getClass());

        Member member = new Member("memberA");

        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);


    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId())
                .orElseThrow(() -> new NoSuchElementException());
        Member findMember2 = memberRepository.findById(member2.getId())
                .orElseThrow(() -> new NoSuchElementException());

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findHelloBy() {
        List<Member> helloBy = memberRepository.findTop3HelloBy();
    }

    @Test
    public void testNamedQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }

    }

    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }

    }

    @Test
    public void returnType() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

//        List<Member> memberList = memberRepository.findList123ByUsername("AAA");
//        Member member = memberRepository.findMemberByUsername("AAA");
//        Optional<Member> memberOpt = memberRepository.findOptionalByUsername("AAA");
//
//        for (Member member1 : memberList) {
//            System.out.println("member1 = " + member1);
//        }
//
//        System.out.println("member = " + member);
//        System.out.println("memberOpt = " + memberOpt);


        // List는 데이터가 없으면 비어있는 컬렉션을 반환 해준다.
//        List<Member> result = memberRepository.findList123ByUsername("asdasd");
//
//        System.out.println("result = " + result);
//        System.out.println("result.size() = " + result.size());


        // 단건 조회는 결과가 없으면 null 을 반환 한다.
        // JPA는 결과가 없으면 NoResultException 이 발생하는데 스프링 데이터 JPA는 null 을 반환해준다.
        // 내부적으로 try-catch 로 감싸서 예외가 발생하면 null 을 반환하도록 작성되어 있다.
//        Member findMember = memberRepository.findMemberByUsername("asdasd");
//        System.out.println("findMember = " + findMember);


        // DB 를 조회했는데 데이터가 없을 수도 있다면 Optional 을 사용하는 것을 권장한다.
        Optional<Member> findMemberOpt = memberRepository.findOptionalByUsername("asdasd");
        System.out.println("findMemberOpt = " + findMemberOpt);


        // 단건 조회인데 결과가 2개 이상이면 Optional 이라도 예외가 발생한다.
        // 원래는 javax.persistence.NonUniqueResultException 예외가 발생하는데 스프링 데이터 JPA가 org.springframework.dao.IncorrectResultSizeDataAccessException 로 변환해서 예외를 반환한다.
        // 변환하는 이유는 리파지토리의 기술은 JPA 또는 몽고DB 등 다른 기술이 될 수 있다. 이걸 사용하는 서비스 계층의 클라이언트는 JPA에 의존하는게 아니라
        // 스프링이 추상화하는 예외에 의존하면 하부의 리파지토리 기술을 JPA, 몽고DB 등 다른 기술로 바꿔도 스프링은 동일하게 IncorrectResultSizeDataAccessException 예외를 반환한다.
        // 그래서 이걸 사용하는 클라이언트 코드를 바꿀 필요가 없다. 그래서 스프링이 이렇게 변환하는 메커니즘으로 동작한다.
        Optional<Member> findMemberAAAOpt = memberRepository.findOptionalByUsername("AAA");
        System.out.println("findMemberAAAOpt = " + findMemberAAAOpt);

    }

    @Test
    public void paging() {

        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;

        // 스프링 데이터 JPA는 페이지를 0부터 가져온다.
        // username을 DESC로 sorting
        // Pageable 인터페이스의 구현은 PageRequest 를 많이 사용한다.
        // 만약 조건이 많다면 Sort.by를 사용하는 것 보다 직접 JPQL를 작성하는 것이 좋다.
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        // Pageable 인터페이스를 사용하면 totalCount를 자동으로 처리해준다.
        // 반환 타입(Slice<Member> page)에 따라서 totalCount 호출 여부가 달라진다.
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        // page를 쉽게 DTO로 변환하는 방법
        // 이 방법으로 외부에 반환해도 괜찮다.
        // Page로 래핑된 MemberDto 그대로 반환해도 JSON 으로 변환된다.
        // Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        // then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();

    }

    @Test
    public void pagingSlice() {

        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;

        // 스프링 데이터 JPA는 페이지를 0부터 가져온다.
        // username을 DESC로 sorting
        // Pageable 인터페이스의 구현은 PageRequest 를 많이 사용한다.
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        // Pageable 인터페이스를 사용하면 totalCount를 자동으로 처리해준다.
        // 반환 타입(Slice<Member> page)에 따라서 totalCount 호출 여부가 달라진다.
        Slice<Member> page = memberRepository.findSliceByAge(age, pageRequest);

        // then
        List<Member> content = page.getContent();

        for (Member member : content) {
            System.out.println("member = " + member);
        }

        assertThat(content.size()).isEqualTo(3);
//        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
//        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();

    }

    @Test
    public void bulkUpdate() {

        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        // when
        int resultCount = memberRepository.bulkAgePlus(20);

        // 벌크 연산이 수행되면 영속성 컨텍스트에 반영하지 못한다.
        // 여기서 다시 select 한다고 하더라도 동일성 보장 때문에 DB에서 가져온 값은 버리게 된다.
        // 그래서 1차 캐시에 남아 있어서 DB에서 새로운 값을 가져오지 못하기 때문에 clear를 해줘야 한다.

        // JPQL 실행문으로 flush 자동 호출이 된다.
        // em.flush();

        // 클리어 하지 않으면 영속성 컨텍스트에서 member5의 age는 변경되지 않은 40 을 유지한다.
        // @Modifying 애노테이션에서 clear를 설정할 수 있다.
        // em.clear();

        List<Member> result = memberRepository.findByUsername("member5");

        Member member5 = result.get(0);

        System.out.println("result.getClass() = " + result.getClass());
        System.out.println("member5.getClass() = " + member5.getClass());
        System.out.println("member5 = " + member5);

        // then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() {
        // given
        // member1 -> teamA
        // member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // when
//        List<Member> members = memberRepository.findAll();
//        List<Member> members = memberRepository.findMemberFetchJoin();
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");

        for (Member member : members) {
            System.out.println("member.getUsername() = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint() {
        // given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        // when
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");

        em.flush();
    }

    @Test
    public void lock() {
        // given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        // when
        List<Member> result = memberRepository.findLockByUsername("member1");
    }

    @Test
    public void callCustom() {
        List<Member> result = memberRepository.findMemberCustom();
    }

    @Test
    public void projections() {

        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        memberRepository.save(m1);
        memberRepository.save(m2);

        em.flush();
        em.clear();

//        List<UsernameOnly> result = memberRepository.findProjectionsByUsername("m1");
//        List<UsernameOnlyDto> result = memberRepository.findProjectionsByUsername("m1");
//        List<UsernameOnlyDto> result = memberRepository.findProjectionsByUsername("m1", UsernameOnlyDto.class);
        List<NestedClosedProjections> result = memberRepository.findProjectionsByUsername("m1", NestedClosedProjections.class);

        for (NestedClosedProjections nestedClosedProjections : result) {
            String username = nestedClosedProjections.getUsername();
            System.out.println("username = " + username);

            String teamName = nestedClosedProjections.getTeam().getName();
            System.out.println("teamName = " + teamName);
        }

    }

    @Test
    public void nativeQuery() {

        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        memberRepository.save(m1);
        memberRepository.save(m2);

        em.flush();
        em.clear();

        // when
//        Member result = memberRepository.findByNativeQuery("m1");
        Page<MemberProjection> result = memberRepository.findByNativeProjection(PageRequest.of(0, 10));

        List<MemberProjection> content = result.getContent();

        for (MemberProjection memberProjection : content) {
            System.out.println("memberProjection.getUsername() = " + memberProjection.getUsername());
            System.out.println("memberProjection.getTeamName() = " + memberProjection.getTeamName());
        }

        // then
    }

}


