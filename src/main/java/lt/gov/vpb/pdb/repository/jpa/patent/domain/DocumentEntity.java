package lt.gov.vpb.pdb.repository.jpa.patent.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "document")
@Getter
@Setter
public class DocumentEntity {

    @Id
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "DOSSIER_ID")
    private DossierEntity dossier;

    @Column(name = "DOC_TYPE")
    private Long type;

    @Column(name = "FILE_NAME")
    private String fileName;

    @Column(name = "CONTENT_SIZE")
    private Long size;

    @Column(name = "CONTENT")
    @Lob
    @Basic(fetch=LAZY)
    private byte[] content;

}
