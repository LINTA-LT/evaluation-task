package lt.gov.vpb.pdb.repository.jpa.patent.domain.Views;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "patent_status_view")
@Getter
@Setter
public class PatentStatusView {

    @Id
    @Column(name = "id")
    private String combinedStatusIds;

    @Column(name = "description_lt")
    private String descriptionLt;

    @Column(name = "description_en")
    private String descriptionEn;

    @Column(name = "count")
    private Long count;
}
