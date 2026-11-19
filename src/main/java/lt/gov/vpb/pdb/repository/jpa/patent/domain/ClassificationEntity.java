package lt.gov.vpb.pdb.repository.jpa.patent.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ipccpc")
@Getter
@Setter
public class ClassificationEntity {

    @Id
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "DOSSIER_ID")
    private DossierEntity dossier;

    @Column(name = "IPCCPC_CODE")
    private String ipcCode;

    @Column(name = "VERSION")
    private String version;

    @Column(name = "SEQ_NUM")
    private Long seqNum;

}
