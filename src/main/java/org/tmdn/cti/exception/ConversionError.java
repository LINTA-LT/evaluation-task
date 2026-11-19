package org.tmdn.cti.exception;

import lombok.Getter;
import lombok.Setter;
import lt.gov.vpb.pdb.springbatch.opendata.IPRightType;

@Getter
@Setter
public class ConversionError {

	private String applicationNumber;
	private String errorDescription;
	private IPRightType type;

	public ConversionError(String applicationNumber, String errorDescription, IPRightType type) {
		super();
		this.applicationNumber = applicationNumber;
		this.errorDescription = errorDescription;
		this.type = type;
	}

	public String toString() {
		return "\r\n" + this.getType()
				+" with Application number " + applicationNumber
				+ " failed to be converted to xml. " + errorDescription;

	}
	

}
