package iprules.service.validator;

import iprules.rest.dto.IpFilterRuleDTO;
import org.junit.jupiter.api.Test;

import static iprules.service.Constants.*;
import static iprules.service.validator.IpFilterRuleValidator.hasValidIpAddresses;
import static org.assertj.core.api.Assertions.assertThat;

public class IpFilterRuleValidatorTest {

    @Test
    void testValidSourceDestinationIps() {
        IpFilterRuleDTO ruleDTO = IpFilterRuleDTO.builder()
                .sourceStartIp(SOURCE_START_IP)
                .sourceEndIp(SOURCE_END_IP)
                .destinationStartIp(DESTINATION_START_IP)
                .destinationEndIp(DESTINATION_END_IP)
                .allow(true)
                .build();
        assertThat(hasValidIpAddresses(ruleDTO))
                .isTrue();

    }

    @Test
    void testValidSubnetIps() {
        IpFilterRuleDTO ruleDTO = IpFilterRuleDTO.builder()
                .sourceSubnetIp(SOURCE_SUBNET_IP)
                .destinationSubnetIp(DESTINATION_SUBNET_IP)
                .allow(true)
                .build();
        assertThat(hasValidIpAddresses(ruleDTO))
                .isTrue();

    }

    @Test
    void testNullSourceSubnetReturnsFalse() {
        IpFilterRuleDTO ruleDTO = IpFilterRuleDTO.builder()
                .destinationSubnetIp(DESTINATION_SUBNET_IP)
                .allow(true)
                .build();
        assertThat(hasValidIpAddresses(ruleDTO))
                .isFalse();

    }

    @Test
    void testNullDestinationSubnetReturnsFalse() {
        IpFilterRuleDTO ruleDTO = IpFilterRuleDTO.builder()
                .sourceSubnetIp(SOURCE_SUBNET_IP)
                .allow(true)
                .build();
        assertThat(hasValidIpAddresses(ruleDTO))
                .isFalse();

    }

    @Test
    void testNotValidSourceSubnetIsReturnsFalse() {
        IpFilterRuleDTO ruleDTO = IpFilterRuleDTO.builder()
                .sourceSubnetIp(SOURCE_START_IP)
                .destinationSubnetIp(DESTINATION_SUBNET_IP)
                .allow(true)
                .build();
        assertThat(hasValidIpAddresses(ruleDTO))
                .isFalse();
    }

    @Test
    void testNotValidDestinationSubnetIsReturnsFalse() {
        IpFilterRuleDTO ruleDTO = IpFilterRuleDTO.builder()
                .sourceSubnetIp(SOURCE_SUBNET_IP)
                .destinationSubnetIp(DESTINATION_START_IP)
                .allow(true)
                .build();
        assertThat(hasValidIpAddresses(ruleDTO))
                .isFalse();
    }

    @Test
    void testNotValidSourceStartReturnsFalse() {
        IpFilterRuleDTO ruleDTO = IpFilterRuleDTO.builder()
                .sourceStartIp("SOURCE_START_IP")
                .sourceEndIp(SOURCE_END_IP)
                .destinationStartIp(DESTINATION_START_IP)
                .destinationEndIp(DESTINATION_END_IP)
                .allow(true)
                .build();
        assertThat(hasValidIpAddresses(ruleDTO))
                .isFalse();
    }

    @Test
    void testNotValidSourceEndReturnsFalse() {
        IpFilterRuleDTO ruleDTO = IpFilterRuleDTO.builder()
                .sourceStartIp(SOURCE_START_IP)
                .sourceEndIp("SOURCE_END_IP")
                .destinationStartIp(DESTINATION_START_IP)
                .destinationEndIp(DESTINATION_END_IP)
                .allow(true)
                .build();
        assertThat(hasValidIpAddresses(ruleDTO))
                .isFalse();
    }

    @Test
    void testNotValidDestinationStartReturnsFalse() {
        IpFilterRuleDTO ruleDTO = IpFilterRuleDTO.builder()
                .sourceStartIp(SOURCE_START_IP)
                .sourceEndIp(SOURCE_END_IP)
                .destinationStartIp("DESTINATION_START_IP")
                .destinationEndIp(DESTINATION_END_IP)
                .allow(true)
                .build();
        assertThat(hasValidIpAddresses(ruleDTO))
                .isFalse();
    }
    @Test
    void testNotValidDestinationEndReturnsFalse() {
        IpFilterRuleDTO ruleDTO = IpFilterRuleDTO.builder()
                .sourceStartIp(SOURCE_START_IP)
                .sourceEndIp(SOURCE_END_IP)
                .destinationStartIp(DESTINATION_START_IP)
                .destinationEndIp("DESTINATION_END_IP")
                .allow(true)
                .build();
        assertThat(hasValidIpAddresses(ruleDTO))
                .isFalse();
    }
}
