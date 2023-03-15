package iprules.db;

import iprules.db.model.IpFilterRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IpFilterRuleRepository extends JpaRepository<IpFilterRule, Long> {
}