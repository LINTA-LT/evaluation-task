package org.tmdn.cti.repository.jpa.converter;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractConverter<S,T, W> implements Converter<S, T, W> {

    @Override
    public W convert(List<S> object) {
        if (object == null || object.isEmpty()) {
            return null;
        } else {
            List<T> list = new ArrayList<>();

            for (S o : object) {
                if (o != null) {
                	T t = convert(o);
                	if(t!=null)	{
                		list.add(t);
                	}
                }
            }

            return wrap(list);
        }
    }

    protected abstract W wrap(List<T> list);
}
