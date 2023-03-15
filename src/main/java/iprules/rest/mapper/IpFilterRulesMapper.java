package iprules.rest.mapper;

import iprules.db.model.IpFilterRule;
import iprules.rest.dto.IpFilterRuleDTO;
import org.mapstruct.Mapper;

import static org.hibernate.internal.util.StringHelper.isEmpty;
import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;

@Mapper(
        componentModel = "spring",
        injectionStrategy = CONSTRUCTOR)
public interface IpFilterRulesMapper {

    IpFilterRuleDTO mapToDTO(IpFilterRule rule);

    IpFilterRule mapToEntity(IpFilterRuleDTO ruleDTO);

    default String emptyToNull(String input) {
        return isEmpty(input) ? null : input;
    }

}
