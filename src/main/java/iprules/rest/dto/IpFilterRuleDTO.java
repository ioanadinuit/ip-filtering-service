package iprules.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class IpFilterRuleDTO {
    @JsonProperty
    private Long id;
    @JsonProperty
    private String sourceStartIp;
    @JsonProperty
    private String sourceEndIp;
    @JsonProperty
    private String sourceSubnetIp;
    @JsonProperty
    private String destinationStartIp;
    @JsonProperty
    private String destinationEndIp;
    @JsonProperty
    private String destinationSubnetIp;
    @JsonProperty
    private boolean allow;
}