package org.example.tamaapi.query;

import org.example.tamaapi.domain.user.MemberAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface MemberAddressQueryRepository extends JpaRepository<MemberAddress, Long> {

    boolean existsByMemberIdAndZipCodeAndStreetAndDetail(Long memberId, String zipCode, String Street, String detail);


    boolean existsByMemberId(Long memberId);

    List<MemberAddress> findAllByMemberId(Long memberId);

    Optional<MemberAddress> findByMemberIdAndIsDefault(Long memberId, Boolean isDefault);
}
