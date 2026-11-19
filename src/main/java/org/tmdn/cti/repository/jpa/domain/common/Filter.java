package org.tmdn.cti.repository.jpa.domain.common;

import java.util.ArrayList;
import java.util.List;

public class Filter {
    /**
     * Just a utility class, no constructor available to outside world
     */
    private Filter() {

    }
    /**
     * Filters inactive objects out of collection
     *
     * @param collection collection of objects of class <T>
     * @param <T> class extending {@link BaseEntity}
     * @return new collection containing only active entries
     */
    public static <T extends BaseEntity> List<T> active(List<T> collection) {
        if (collection == null) {
            return null;
        }

        List<T> result = new ArrayList<T>();

        for (T removable : collection) {
            if (removable.isActive()) {
                result.add(removable);
            }
        }

        return result;
    }
}
