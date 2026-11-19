package org.tmdn.cti.exception;

public class EntityNotFoundException extends CTIEntityException {
    
	private static final long serialVersionUID = -8293558730563235147L;

	public EntityNotFoundException(String message, String id) {
        super(message, id);
    }
}
