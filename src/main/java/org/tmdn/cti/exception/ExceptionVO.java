package org.tmdn.cti.exception;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ExceptionVO {
    @XmlElement
    private String id;
    @XmlElement
    private String message;

    /**
     * Default constructor only for JAX-B serialization
     */
    public ExceptionVO() {
    }

    public ExceptionVO(String id, String message) {
        this.id = id;
        this.message = message;
    }

    public static ExceptionVO create(CTIEntityException e) {
        return new ExceptionVO(e.getId(), e.getMessage());
    }

    public static ExceptionVO create(CTIException e) {
        return new ExceptionVO(null, e.getMessage());
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
}
