package iprules.db.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

import javax.persistence.*;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@Entity
@Table(name = "ip_filter_rule")
public class IpFilterRule {
    @Tolerate
    public IpFilterRule() {}
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_start_ip")
    private String sourceStartIp;

    @Column(name = "source_end_ip")
    private String sourceEndIp;

    @Column(name = "source_subnet_ip")
    private String sourceSubnetIp;

    @Column(name = "destination_start_ip")
    private String destinationStartIp;

    @Column(name = "destination_end_ip")
    private String destinationEndIp;

    @Column(name = "destination_subnet_ip")
    private String destinationSubnetIp;

    @Column(name = "allow")
    private Boolean allow;
}