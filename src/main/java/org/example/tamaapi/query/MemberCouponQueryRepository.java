package org.example.tamaapi.query;

import org.example.tamaapi.domain.user.coupon.MemberCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface MemberCouponQueryRepository extends JpaRepository<MemberCoupon, Long> {

    @Query("""
            select m from MemberCoupon m join fetch m.coupon c
            where m.member.id = :memberId and m.isUsed = false and c.expiresAt >= now() 
            """)
    List<MemberCoupon> findNotExpiredAndUnusedCouponsByMemberId(Long memberId);

    @Query("select m from MemberCoupon m join fetch m.coupon c where m.id =:memberCouponId")
    Optional<MemberCoupon> findWithById(Long memberCouponId);

    boolean existsByMemberIdAndIsUsedIsFalse(Long memberId);

}
