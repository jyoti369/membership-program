package com.firstclub.membership.repository;

import com.firstclub.membership.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId AND o.orderDate >= :startDate")
    long countOrdersByUserSince(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT COALESCE(SUM(o.orderValue), 0) FROM Order o WHERE o.user.id = :userId AND o.orderDate >= :startDate")
    BigDecimal sumOrderValueByUserSince(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate);
}
