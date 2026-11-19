package lt.gov.vpb.pdb.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class PaginatedList<T> {

    private Collection<T> data;
    private long totalItems;
    private long offset;
    private long size;

    public PaginatedList(Collection<T> data, long totalHits, long offset, long size) {
        this.data = data;
        this.totalItems = totalHits;
        this.offset = offset;
        this.size = size;
    }
}
