package iprules.service;

import iprules.db.IpFilterRuleRepository;
import iprules.db.RulesFilterPreparedStatement;
import iprules.db.model.IpFilterRule;
import iprules.rest.dto.IpAllowedDTO;
import iprules.rest.dto.IpFilterRuleDTO;
import iprules.rest.mapper.IpFilterRulesMapper;
import iprules.service.exception.IpAddressValidatorException;
import iprules.service.exception.IpRuleMissingException;
import iprules.service.exception.SqlErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import static iprules.service.validator.IpFilterRuleValidator.hasValidIpAddresses;

@Slf4j
@Service
@RequiredArgsConstructor
public class IpFilterRuleService {
    private final IpFilterRuleRepository repository;
    private final RulesFilterPreparedStatement statement;
    private final IpFilterRulesMapper mapper;

    public IpAllowedDTO isAllowed(String sourceIp, String destinationIp) {
        try {
            log.info("Filtering source IP: {} and destination IP: {} ", sourceIp, destinationIp);
            Optional<Boolean> isAllowed = statement.isIpAllowed(sourceIp, destinationIp);
            if (!isAllowed.isPresent()) {
                isAllowed = Optional.of(false);
            }
            return new IpAllowedDTO(isAllowed.get());
        } catch (NoSuchElementException e) {
            log.trace("No rules to match {} {} exist", sourceIp, destinationIp);
            return new IpAllowedDTO(false);
        } catch (SQLException e) {
            log.error("Failed to execute query for ips: {} {} {} {} {}",
                    sourceIp, destinationIp, e.getSQLState(), e.getErrorCode(), e.getMessage());
            throw new SqlErrorException(e.getMessage());
        }
    }

    public IpFilterRuleDTO save(IpFilterRuleDTO ipFilterRuleDTO) {
        if (!hasValidIpAddresses(ipFilterRuleDTO)) {
            String errorMsg = "Received IP rules that are not valid: " + ipFilterRuleDTO.toString();
            log.error(errorMsg);
            throw new IpAddressValidatorException(errorMsg);
        }
        IpFilterRule rule = repository.save(mapper.mapToEntity(ipFilterRuleDTO));
        return mapper.mapToDTO(rule);
    }

    public IpFilterRuleDTO findById(Long id) {
        IpFilterRule rule = repository.findById(id)
                .orElseThrow(() -> new IpRuleMissingException("Rule not found for id: " + id));
        return mapper.mapToDTO(rule);
    }

    public void deleteById(Long id) {
        IpFilterRule rule = repository.findById(id).orElseThrow(() ->
                new IpRuleMissingException("Not found rule id: " + id));
        repository.delete(rule);
    }
}
