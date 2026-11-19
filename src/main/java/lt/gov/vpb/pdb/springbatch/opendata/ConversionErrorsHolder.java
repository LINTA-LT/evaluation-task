package lt.gov.vpb.pdb.springbatch.opendata;

import org.tmdn.cti.exception.ConversionError;

import java.util.List;

public interface ConversionErrorsHolder {

	List<ConversionError> getConversionErrors();

}
