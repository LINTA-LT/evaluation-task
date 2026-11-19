package org.tmdn.cti.exception;

public class CTIException extends RuntimeException {
    
	private static final long serialVersionUID = 3761807994511754593L;

	public CTIException(Throwable cause) {
        super(cause);
    }

    public CTIException(String s, Exception e) {
        super(s, e);
    }
}
