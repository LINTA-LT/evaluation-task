package org.tmdn.cti.repository.jpa.domain.trademark;

import lombok.Data;
import org.tmdn.cti.repository.jpa.domain.common.Image;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "MARKIMAGE")
@Data
public class TmImage extends Image {

    @Id
    @Column(name = "MARKIMAGEID")
    private Long id;

    @Column(name = "MARKID")
    private Long markId;

    @Column(name = "INDCOLOUR")
    private boolean colourIndicator;

    @Column(name = "COLOURCLAIMTEXT")
    private String colourClaimedText;
   
    @OneToMany
    @JoinColumn(name = "MARKIMAGEID")
    private List<ImageCategory> imageCategories;
    
}
