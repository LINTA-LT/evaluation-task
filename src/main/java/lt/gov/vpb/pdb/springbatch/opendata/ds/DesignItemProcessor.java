package lt.gov.vpb.pdb.springbatch.opendata.ds;

import lt.gov.vpb.pdb.springbatch.opendata.IPRightType;
import lt.gov.vpb.pdb.springbatch.opendata.ds.converters.DesignConverter;
import lt.gov.vpb.pdb.springbatch.opendata.file.FileDetails;
import lt.gov.vpb.pdb.springbatch.util.JAXBObjectToFileDetailsConverter;
import lt.vpb.open_data.schemas.design.v1_2.DesignType;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.tmdn.cti.exception.ConversionError;
import org.tmdn.cti.repository.jpa.domain.design.Design;

import java.util.ArrayList;
import java.util.List;

import static lt.gov.vpb.pdb.springbatch.opendata.IPRightType.DESIGN;

public class DesignItemProcessor implements ItemProcessor<Design, FileDetails> {

	@Autowired
	@Qualifier("designToFileDetailsConverter")
	private JAXBObjectToFileDetailsConverter<DesignType> jaxbObjectToFileDetailsConverter;

	@Autowired
	private DesignConverter designConverter;

	private List<ConversionError> conversionErrors = new ArrayList<>();

	@Override
	public FileDetails process(Design item) throws Exception {

		DesignType design = designConverter.convert(item, conversionErrors);
		if (design == null) {
			// filters the record
			// framework will detect that the result is "null" and avoid adding that item to
			// the list of records delivered to the ItemWriter
			return null;
		}
		FileDetails fileDetails = jaxbObjectToFileDetailsConverter.convert(design, "");
		if(fileDetails.getValidationResult().isFailed()){
			conversionErrors.add(new ConversionError(fileDetails.getValidationResult().getApplicationNumber(),
					fileDetails.getValidationResult().getErrorMessage(),
					DESIGN));
		}
		return fileDetails;
	}

	public List<ConversionError> getConversionErrors() {
		return conversionErrors;
	}

}
