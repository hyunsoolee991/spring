package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Test
    public void 회원가입() throws Exception {
        // given
        Member member = new Member();
        member.setName("lee");

        // when
        Long savedId = memberService.join(member);

        // then
//        여기서 호출하면 flush 후에 테스트가 종료될 때 롤백됨
//        em.flush();
        assertEquals(memberRepository.findOne(savedId), member);
    }

    @Test
    public void 중복_회원_예외() throws Exception {
        // given
        Member member1 = new Member();
        member1.setName("lee");

        Member member2 = new Member();
        member2.setName("lee");

        // when
        memberService.join(member1);

        // then
        // memberService.join(member2)에서 IllegalStateException 예외가 발생하면 테스트 통과
        assertThrows(IllegalStateException.class, () -> memberService.join(member2));
    }

}