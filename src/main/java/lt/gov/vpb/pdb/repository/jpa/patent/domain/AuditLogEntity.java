package lt.gov.vpb.pdb.repository.jpa.patent.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
@Getter
@Setter
public class AuditLogEntity {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

}
