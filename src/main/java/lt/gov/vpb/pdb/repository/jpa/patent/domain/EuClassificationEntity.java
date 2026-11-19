package lt.gov.vpb.pdb.repository.jpa.patent.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "eu_ipc")
@Getter
@Setter
public class EuClassificationEntity {

    @Id
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "EU_DOSSIER_ID")
    private EuDossierEntity dossier;

    @Column(name = "IPC_CLASS")
    private String ipcClass;

    @Column(name = "VERSION")
    private String version;

    @Column(name = "SEQ_NUM")
    private Long seqNum;

}
