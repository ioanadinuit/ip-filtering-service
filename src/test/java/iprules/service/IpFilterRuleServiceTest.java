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
import iprules.service.validator.IpFilterRuleValidator;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Optional;

import static iprules.service.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class IpFilterRuleServiceTest {

    @Mock
    private IpFilterRuleRepository repository;

    @Mock
    private RulesFilterPreparedStatement statement;

    @Mock
    private IpFilterRulesMapper mapper;

    @InjectMocks
    private IpFilterRuleService service;

    @Test
    void whenSaveGivenValidRulesProvidedShouldSave() {
        try(MockedStatic<IpFilterRuleValidator> utilities = Mockito.mockStatic(IpFilterRuleValidator.class)) {
            IpFilterRuleDTO ruleDTO = IpFilterRuleDTO.builder()
                    .sourceSubnetIp(SOURCE_SUBNET_IP)
                    .destinationSubnetIp(DESTINATION_SUBNET_IP)
                    .allow(true)
                    .build();
            IpFilterRule rule = IpFilterRule.builder()
                    .sourceSubnetIp(SOURCE_SUBNET_IP)
                    .destinationSubnetIp(DESTINATION_SUBNET_IP)
                    .allow(true)
                    .build();
            utilities.when(() -> IpFilterRuleValidator.hasValidIpAddresses(ruleDTO))
                    .thenReturn(true);
            when(mapper.mapToEntity(ruleDTO)).thenReturn(rule);
            when(repository.save(rule)).thenReturn(rule);
            when(mapper.mapToDTO(rule)).thenReturn(ruleDTO);
            IpFilterRuleDTO resultRuleDTO = service.save(ruleDTO);
            assertThat(resultRuleDTO).isEqualTo(ruleDTO);
            verify(repository).save(rule);
            verify(mapper).mapToEntity(ruleDTO);
            verify(mapper).mapToDTO(rule);
        }
    }

    @Test
    void whenSaveGivenNoValidRulesProvidedShouldThrowException() {
        try(MockedStatic<IpFilterRuleValidator> utilities = Mockito.mockStatic(IpFilterRuleValidator.class)) {
            IpFilterRuleDTO ruleDTO = IpFilterRuleDTO.builder()
                    .allow(true)
                    .build();
            IpFilterRule rule = IpFilterRule.builder()
                    .allow(true)
                    .build();
            utilities.when(() -> IpFilterRuleValidator.hasValidIpAddresses(ruleDTO))
                    .thenReturn(false);

            assertThatThrownBy(() -> service.save(ruleDTO))
                    .isInstanceOf(IpAddressValidatorException.class);
            verify(repository, never()).save(rule);
            verify(mapper, never()).mapToEntity(ruleDTO);
            verify(mapper, never()).mapToDTO(rule);
        }
    }

    @Test
    void whenSaveGivenExistingIdShouldReturn() {
        IpFilterRule rule = IpFilterRule.builder()
                .sourceSubnetIp(SOURCE_SUBNET_IP)
                .destinationSubnetIp(DESTINATION_SUBNET_IP)
                .allow(true)
                .build();
        IpFilterRuleDTO ruleDTO = IpFilterRuleDTO.builder()
                .sourceSubnetIp(SOURCE_SUBNET_IP)
                .destinationSubnetIp(DESTINATION_SUBNET_IP)
                .allow(true)
                .build();
        when(repository.findById(1l)).thenReturn(Optional.of(rule));
        when(mapper.mapToDTO(rule)).thenReturn(ruleDTO);
        IpFilterRuleDTO savedRuleDTO = service.findById(1l);
        assertThat(savedRuleDTO).isEqualTo(ruleDTO);

        verify(repository).findById(1l);
        verify(mapper).mapToDTO(rule);
    }

    @Test
    void whenSaveGivenNotExistingIdShouldThrowException() {
        when(repository.findById(1l)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findById(1l))
                .isInstanceOf(IpRuleMissingException.class);

        verify(repository).findById(1l);
        verify(mapper, never()).mapToDTO(any());
    }

    @Test
    void whenDeleteByIdGivenExistingIdShouldDelete() {
        IpFilterRule rule = IpFilterRule.builder()
                .sourceSubnetIp(SOURCE_SUBNET_IP)
                .destinationSubnetIp(DESTINATION_SUBNET_IP)
                .allow(true)
                .build();
        when(repository.findById(1l)).thenReturn(Optional.of(rule));
        doNothing().when(repository).delete(rule);
        service.deleteById(1l);
        verify(repository).findById(1l);
        verify(repository).delete(rule);
    }

    @Test
    void whenDeleteByIdGivenNotExistingIdShouldThrowException() {
        when(repository.findById(1l)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.deleteById(1l))
                .isInstanceOf(IpRuleMissingException.class);
        verify(repository).findById(1l);
        verify(repository,never()).delete(any());
    }

    @Test
    void whenGetIsAllowedGivenAllowedIpsShouldReturnTrue() throws SQLException {
        when(statement.isIpAllowed(SOURCE_IP, DESTINATION_IP))
                .thenReturn(Optional.of(true));
        IpAllowedDTO ipAllowedDTO = service.isAllowed(SOURCE_IP, DESTINATION_IP);
        assertThat(ipAllowedDTO).isNotNull();
        assertThat(ipAllowedDTO.getIpsAllowed()).isTrue();
        verify(statement).isIpAllowed(SOURCE_IP, DESTINATION_IP);
    }

    @Test
    void whenGetIsAllowedGivenNotAllowedIpsShouldReturnFalse() throws SQLException {
        when(statement.isIpAllowed(SOURCE_IP, DESTINATION_IP))
                .thenReturn(Optional.of(false));
        IpAllowedDTO ipAllowedDTO = service.isAllowed(SOURCE_IP, DESTINATION_IP);
        assertThat(ipAllowedDTO).isNotNull();
        assertThat(ipAllowedDTO.getIpsAllowed()).isFalse();
        verify(statement).isIpAllowed(SOURCE_IP, DESTINATION_IP);
    }

    @Test
    void whenGetIAllowedGivenNotValueFoundIpsShouldReturnFalse() throws SQLException {
        when(statement.isIpAllowed(SOURCE_IP, DESTINATION_IP))
                .thenReturn(Optional.empty());
        IpAllowedDTO ipAllowedDTO = service.isAllowed(SOURCE_IP, DESTINATION_IP);
        assertThat(ipAllowedDTO).isNotNull();
        assertThat(ipAllowedDTO.getIpsAllowed()).isFalse();
        verify(statement).isIpAllowed(SOURCE_IP, DESTINATION_IP);
    }

    @Test
    void whenGetIAllowedGivenExceptionThrownShouldReturnFalse() throws SQLException {
       when(statement.isIpAllowed(SOURCE_IP, DESTINATION_IP))
               .thenThrow(new NoSuchElementException());
        IpAllowedDTO ipAllowedDTO = service.isAllowed(SOURCE_IP, DESTINATION_IP);
        assertThat(ipAllowedDTO).isNotNull();
        assertThat(ipAllowedDTO.getIpsAllowed()).isFalse();
        verify(statement).isIpAllowed(SOURCE_IP, DESTINATION_IP);
    }

    @Test
    void whenGetIAllowedGivenSqlExceptionThrownShouldThrowException() throws SQLException {
        when(statement.isIpAllowed(SOURCE_IP, DESTINATION_IP))
                .thenThrow(new SQLException());
        assertThatThrownBy(()-> service.isAllowed(SOURCE_IP, DESTINATION_IP))
                .isInstanceOf(SqlErrorException.class);
        verify(statement).isIpAllowed(SOURCE_IP, DESTINATION_IP);
    }

}
