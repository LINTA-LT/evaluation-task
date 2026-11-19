package org.tmdn.cti.repository.jpa.converter;

import java.util.List;

public interface Converter<S,T,W> {

    T convert(S object);

    W convert(List<S> object);

}
