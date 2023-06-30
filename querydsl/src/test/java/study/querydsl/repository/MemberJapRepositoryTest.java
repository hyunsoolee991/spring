package study.querydsl.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import java.util.List;

import static org.apache.coyote.http11.Constants.a;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberJapRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    MemberJapRepository memberJapRepository;

    @Test
    public void basicTest() {

        Member member = new Member("member1", 10);
        memberJapRepository.save(member);

        Member findMember = memberJapRepository.findById(member.getId()).get();
        // 같은 영속성 컨텍스트라서 객체 주소가 같아야 한다.
        assertThat(findMember).isEqualTo(member);

        List<Member> result1 = memberJapRepository.findAll();
        assertThat(result1).containsExactly(member);

        List<Member> result2 = memberJapRepository.findByUsername("member1");
        assertThat(result2).containsExactly(member);
    }

    @Test
    public void basicTest_Querydsl() {

        Member member = new Member("member1", 10);
        memberJapRepository.save(member);

        Member findMember = memberJapRepository.findById(member.getId()).get();
        // 같은 영속성 컨텍스트라서 객체 주소가 같아야 한다.
        assertThat(findMember).isEqualTo(member);

        List<Member> result1 = memberJapRepository.findAll_Querydsl();
        assertThat(result1).containsExactly(member);

        List<Member> result2 = memberJapRepository.findByUsername_Querydsl("member1");
        assertThat(result2).containsExactly(member);
    }

    @Test
    public void searchTest() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        MemberSearchCondition condition = new MemberSearchCondition();

//        조건이 모두 null 이라면 모든 데이터틑 join 해서 가져오게 된다. 이러면 성능상 문제가 있어서 조건이 하나라도 있는게 좋거나 데이터 가져오는 개수를 리미트하는게 좋다.
//        condition.setAgeGoe(35);
//        condition.setAgeLoe(40);
        condition.setTeamName("teamB");

        List<MemberTeamDto> result = memberJapRepository.searchByBuilder(condition);

        for (MemberTeamDto memberTeamDto : result) {
            System.out.println("memberTeamDto = " + memberTeamDto);
        }

//        assertThat(result).extracting("username").containsExactly("member4");
        assertThat(result).extracting("username").containsExactly("member3", "member4");

    }

    @Test
    public void search() {

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        MemberSearchCondition condition = new MemberSearchCondition();

        condition.setAgeGoe(35);
        condition.setAgeLoe(40);
        condition.setTeamName("teamB");

        List<MemberTeamDto> result = memberJapRepository.search(condition);

        for (MemberTeamDto memberTeamDto : result) {
            System.out.println("memberTeamDto = " + memberTeamDto);
        }

        assertThat(result).extracting("username").containsExactly("member4");

    }

}