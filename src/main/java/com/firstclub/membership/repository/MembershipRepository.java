package com.firstclub.membership.repository;

import com.firstclub.membership.entity.Membership;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.enums.MembershipStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {

    Optional<Membership> findByUser(User user);

    Optional<Membership> findByUserId(Long userId);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT m FROM Membership m WHERE m.user.id = :userId")
    Optional<Membership> findByUserIdWithLock(@Param("userId") Long userId);

    boolean existsByUserAndStatus(User user, MembershipStatus status);
}
