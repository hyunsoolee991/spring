package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    // 단순히 페이징을 하는지 확인하기 위한 메서드
    List<Member> findTop3HelloBy();

    // @Query 애노테이션이 없어도 username과 일치하는 NamedQuery를 자동으로 찾는다. 만약 NamedQuery가 없다면 메소드 쿼리가 실행된다. 순서는 바꿀 수 있다.
    // @Query(name = "Member.findByUsername") // Member 엔티티의 @NamedQuery 쿼리의 name 과 매칭된다.
    List<Member> findByUsername(@Param("username") String username); // @Param의 "username"은 쿼리에 넘길 파라미터가 된다.

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    List<Member> findList123ByUsername(String username); // 컬렉션

    Member findMemberByUsername(String username); // 단건

    Optional<Member> findOptionalByUsername(String username); // 단건 Optional

    // Pageable 인터페이스를 구현
    //
    @Query(value = "select m from Member m left join m. team t",
            countQuery = "select count(m) from Member m")
    // countQuery를 직접 작성하지 않으면 left join이 필요하지 않은 경우에 쿼리에 포함되어 복잡한 쿼리인 경우 성능에 문제가 생길 수 있다.
    Page<Member> findByAge(int age, Pageable pageable);

    Slice<Member> findSliceByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true) // 이 애노테이션이 있어야 수정 쿼리가 실행된다. 없으면 예외가 발생한다.
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    //    @EntityGraph(attributePaths = {"team"})
    @EntityGraph("Member.all")
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);

//    인터페이스로 작성한 프로젝션
//    List<UsernameOnly> findProjectionsByUsername(@Param("username") String username);

//    클래스로 작성한 프로젝션
//    List<UsernameOnlyDto> findProjectionsByUsername(@Param("username") String username);

    //    동적 프로젝션
    <T> List<T> findProjectionsByUsername(@Param("username") String username, Class<T> type);

    // 네이티브 쿼리
    @Query(value = "select * from member where username = ?", nativeQuery = true)
    Member findByNativeQuery(String username);

    // 페이징 네이티브 쿼리
    // MemberProjection 에 선언한 이름과 쿼리 컬럼의 이름을 매칭시킨다.
    @Query(value = "select m.member_id as id, m.username, t.name as teamName " +
            "from member m " +
            "left join team t",
            countQuery = "select count(*) from member",
            nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);
}
