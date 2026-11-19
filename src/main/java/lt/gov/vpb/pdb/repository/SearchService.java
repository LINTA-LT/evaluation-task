package lt.gov.vpb.pdb.repository;

import lt.gov.vpb.pdb.form.PaginatedSortedForm;
import lt.gov.vpb.pdb.model.Details;
import lt.gov.vpb.pdb.model.ListItem;
import lt.gov.vpb.pdb.model.PaginatedList;

/**
 * Search Provider
 *
 * @param <S> search form type
 * @param <L> listItem type
 * @param <D> details type
 */
public interface SearchService<S extends PaginatedSortedForm, L extends ListItem, D extends Details> {

    PaginatedList<L> search(S searchForm);

    D details(String id);

}
