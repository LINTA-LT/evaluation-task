package lt.gov.vpb.pdb.springbatch.opendata.file;

import lt.gov.vpb.pdb.springbatch.opendata.IPRightType;

/**
 * Holds the validation result of the xml file of a design generated during the syncing process of central index
 *
 */
public class ValidationResult {
	
	private String applicationNumber;
	private boolean failed;
	private String errorMessage;
	private final ValidationResultType type;	
	private IPRightType rightType;
	
	
	public enum ValidationResultType {
		INVALID_XML,
		DUPLICATED_FILENAME
	}

	public String getApplicationNumber() {
		return applicationNumber;
	}
	
	public void setApplicationNumber(String applicationNumber) {
		this.applicationNumber = applicationNumber;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public ValidationResult(String applicationNumber, String errorMessage, ValidationResultType type, IPRightType rightType) {
		super();
		this.applicationNumber = applicationNumber;
		this.errorMessage = errorMessage;
		this.type = type;
		this.rightType = rightType;
	}
	
	public ValidationResult(String applicationNumber, String errorMessage, boolean failed, ValidationResultType type) {
		super();
		this.applicationNumber = applicationNumber;
		this.errorMessage = errorMessage;
		this.failed=failed;
		this.type = type;
	}
	
	public ValidationResult(String errorMessage, boolean failed, ValidationResultType type) {
		super();
		this.errorMessage = errorMessage;
		this.failed=failed;
		this.type = type;
	}
	
	public boolean isFailed() {
		return failed;
	}
	
	public void setFailed(boolean failed) {
		this.failed = failed;
	}
	
	public ValidationResult() {
		super();
		this.type = ValidationResultType.INVALID_XML;
	}
	
	public String toString() {
		if(failed)  {
			switch(type) {
				case DUPLICATED_FILENAME:
					return "\r\nDuplicate filename found and is going to be excluded from the zips: "+errorMessage; 
				default:
					return "\r\nThe xml with Application number "+applicationNumber+" is invalid. "+errorMessage; 
			}
		} else {
			return "";
		}
	}

	public ValidationResultType getType() {
		return type;
	}
	
	
	public IPRightType getRightType() {
		return rightType;
	}

	public void setRightType(IPRightType rightType) {
		this.rightType = rightType;
	}

}
