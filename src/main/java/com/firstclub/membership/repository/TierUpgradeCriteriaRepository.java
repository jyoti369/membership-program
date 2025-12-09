package com.firstclub.membership.repository;

import com.firstclub.membership.entity.TierUpgradeCriteria;
import com.firstclub.membership.enums.TierLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TierUpgradeCriteriaRepository extends JpaRepository<TierUpgradeCriteria, Long> {
    Optional<TierUpgradeCriteria> findByTargetTier(TierLevel targetTier);
    List<TierUpgradeCriteria> findByActiveTrue();
}
