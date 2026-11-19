package lt.gov.vpb.pdb.repository.jpa.patent.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "STATUS")
@Getter
@Setter
public class StatusEntity {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "CODE")
    private Long code;

    @Column(name = "DESCRIPTION_LT")
    private String descriptionLt;

    @Column(name = "DESCRIPTION_EN")
    private String descriptionEn;

    @Column(name = "EPO_STATUS")
    private String epoStatus;

    @ManyToOne
    @JoinColumn(name = "SHOW_STATUS")
    private StatusEntity showStatus;
}
