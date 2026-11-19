package lt.gov.vpb.pdb.springbatch.opendata.tm;


import lombok.extern.slf4j.Slf4j;
import lt.gov.vpb.pdb.springbatch.opendata.ConversionErrorsHolder;
import lt.gov.vpb.pdb.springbatch.opendata.file.FileDetails;
import lt.gov.vpb.pdb.springbatch.opendata.tm.converters.TrademarkConverter;
import lt.gov.vpb.pdb.springbatch.util.JAXBObjectToFileDetailsConverter;
import lt.vpb.open_data.schemas.trademark.v1_2.TradeMarkType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.tmdn.cti.exception.ConversionError;
import org.tmdn.cti.repository.jpa.domain.trademark.TradeMark;

import java.util.ArrayList;
import java.util.List;

import static lt.gov.vpb.pdb.springbatch.opendata.IPRightType.DESIGN;
import static lt.gov.vpb.pdb.springbatch.opendata.IPRightType.TRADEMARK;

@Slf4j
public class TrademarkItemProcessor implements ConversionErrorsHolder, ItemProcessor<TradeMark, FileDetails> {

    @Autowired
    @Qualifier("trademarkToFileDetailsConverter")
    private JAXBObjectToFileDetailsConverter<TradeMarkType> jaxbObjectToFileDetailsConverter;

    @Autowired
    private TrademarkConverter trademarkConverter;

    private String fileNameDatePrefix;

    private List<ConversionError> conversionErrors = new ArrayList<>();

    @Override
    public FileDetails process(TradeMark item) throws Exception {

        TradeMarkType tm = trademarkConverter.convert(item, conversionErrors);
        if (tm == null) {
            // filters the record
            // framework will detect that the result is "null" and avoid adding that item to
            // the list of records delivered to the ItemWriter
            return null;
        }
        FileDetails fileDetails = jaxbObjectToFileDetailsConverter.convert(tm, fileNameDatePrefix);
        try {

            if (fileDetails.getValidationResult().isFailed()) {
                conversionErrors.add(new ConversionError(fileDetails.getValidationResult().getApplicationNumber(),
                        fileDetails.getValidationResult().getErrorMessage(),
                        TRADEMARK));
            }
        } catch (Exception e) {
            log.debug("Exception! trademark : {}", tm.getApplicationNumber());
            log.debug("Exception! fileDetails : {}", fileDetails);
            log.debug("Exception! fileDetails : {}", ToStringBuilder.reflectionToString(fileDetails));
            throw new RuntimeException(e);
        }
        return fileDetails;
    }

    public List<ConversionError> getConversionErrors() {
        return conversionErrors;
    }

    public void setFileNameDatePrefix(String fileNameDatePrefix) {
        this.fileNameDatePrefix = fileNameDatePrefix;
    }

}
