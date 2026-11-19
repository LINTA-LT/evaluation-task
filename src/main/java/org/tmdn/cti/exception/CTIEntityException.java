package org.tmdn.cti.exception;

abstract class CTIEntityException extends Exception {
    
	private static final long serialVersionUID = -5281317994178151719L;
	private final String id;

    CTIEntityException(String message, String id) {
        super(message + " [id=" + id + "]");
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
