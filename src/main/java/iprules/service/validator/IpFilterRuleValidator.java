package iprules.service.validator;

import iprules.rest.dto.IpFilterRuleDTO;
import org.apache.commons.validator.routines.InetAddressValidator;

import static org.apache.logging.log4j.util.Strings.isEmpty;
import static org.apache.logging.log4j.util.Strings.isNotEmpty;

public class IpFilterRuleValidator {

    public static boolean hasValidIpAddresses(IpFilterRuleDTO ipFilterRuleDTO) {
        return hasValidSourceIpRule(ipFilterRuleDTO)
                && hasValidDestinationIpRule(ipFilterRuleDTO);
    }

    public static boolean hasValidDestinationIpRule(IpFilterRuleDTO ipFilterRuleDTO) {
        return hasIpRangeValid(ipFilterRuleDTO.getDestinationStartIp(),
                    ipFilterRuleDTO.getDestinationEndIp(),
                    ipFilterRuleDTO.getDestinationSubnetIp())

               || hasValidMaskRule(ipFilterRuleDTO.getDestinationStartIp(),
                    ipFilterRuleDTO.getDestinationEndIp(),
                    ipFilterRuleDTO.getDestinationSubnetIp());
    }

    public static boolean hasValidSourceIpRule(IpFilterRuleDTO ipFilterRuleDTO) {
        return hasIpRangeValid(ipFilterRuleDTO.getSourceStartIp(),
                    ipFilterRuleDTO.getSourceEndIp(),
                    ipFilterRuleDTO.getSourceSubnetIp())

               || hasValidMaskRule(ipFilterRuleDTO.getSourceStartIp(),
                    ipFilterRuleDTO.getSourceEndIp(),
                    ipFilterRuleDTO.getSourceSubnetIp());
    }

    private static boolean hasIpRangeValid(String startIp, String endIp, String maskIp) {
        InetAddressValidator addressValidator = InetAddressValidator.getInstance();
        return isNotEmpty(startIp) && isNotEmpty(endIp)
                && isEmpty(maskIp)
                && addressValidator.isValidInet4Address(startIp)
                && addressValidator.isValidInet4Address(endIp);
    }

    private static boolean hasValidMaskRule(String startIp, String endIp, String maskIp) {
        return isEmpty(startIp) && isEmpty(endIp) && isNotEmpty(maskIp)
                && hasValidSubnetMask(maskIp);
    }

    private static boolean hasValidSubnetMask(String ipAddress) {
        int slashIndex = ipAddress.indexOf("/");

        // should be a subnet mask
        if (slashIndex < 0) {
            return false;
        }

        String ip = ipAddress.substring(0, slashIndex);
        if (!InetAddressValidator.getInstance().isValidInet4Address(ip)) {
            return false;
        }

        // Check if the subnet mask is valid
        String subnetMask = ipAddress.substring(slashIndex + 1);
        try {
            int prefixLength = Integer.parseInt(subnetMask);
            if (prefixLength < 0 || prefixLength > 32) {
                return false;  // Invalid prefix length
            }
        } catch (NumberFormatException e) {
            return false;  // Invalid subnet mask
        }

        return true;
    }
}
