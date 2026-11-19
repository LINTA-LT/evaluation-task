package lt.gov.vpb.pdb.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;

@Getter
@Setter
public abstract class PaginatedSortedForm {

    private int offset = 0;
    private int size = 50;
    private final static int PAGE_MAX_SIZE = 100;

    /**
     * must be asc or desc; if different values used then search provider implementation should be updated
     */
    @Valid
    private String sortDir = "desc";

    private String sortField;

    public void setSize(int size){
        if(size <= PAGE_MAX_SIZE){
            this.size = size;
        }
    }

}
