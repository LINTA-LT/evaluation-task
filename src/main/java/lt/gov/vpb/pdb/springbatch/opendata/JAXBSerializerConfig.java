package lt.gov.vpb.pdb.springbatch.opendata;


import lombok.extern.slf4j.Slf4j;
import lt.gov.vpb.pdb.springbatch.opendata.ds.DesignJAXBSerializer;
import lt.gov.vpb.pdb.springbatch.opendata.file.FileDetailsCreator;
import lt.gov.vpb.pdb.springbatch.opendata.tm.TmJAXBSerializer;
import lt.gov.vpb.pdb.springbatch.util.JAXBObjectToFileDetailsConverter;
import lt.gov.vpb.pdb.springbatch.util.JAXBSerializer;
import lt.vpb.open_data.schemas.design.v1_2.DesignType;
import lt.vpb.open_data.schemas.trademark.v1_2.TradeMarkType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@Slf4j
@EnableAsync
public class JAXBSerializerConfig {

    @Autowired
    private Environment environment;

    private static final String FILE_EXTENSION = ".xml";

    @Bean(name = "trademarkJaxbSerializer")
    public JAXBSerializer trademarkJaxbSerializer() {
        String validateSchema = environment.getProperty("opendata.xml.validateAgainstSchema");

        if (validateSchema != null && Boolean.valueOf(validateSchema)) {
            log.info("Start validation of the xml against xsd");
            return new TmJAXBSerializer("/xsd/opendata/trademark-v1-2.xsd");
        } else {
            log.info("Validation of the xml against xsd is inactive");
            return new TmJAXBSerializer(null);
        }
    }

    @Bean(name = "designJaxbSerializer")
    public JAXBSerializer designJaxbSerializer() {
        String validateSchema = environment.getProperty("opendata.xml.validateAgainstSchema");

        if (validateSchema != null && Boolean.valueOf(validateSchema)) {
            log.info("Start validation of the xml against xsd");
            return new DesignJAXBSerializer("/xsd/opendata/design-v1-2.xsd");
        } else {
            log.info("Validation of the xml against xsd is inactive");
            return new DesignJAXBSerializer(null);
        }
    }

    @Bean(name = "trademarkToFileDetailsConverter")
    public JAXBObjectToFileDetailsConverter<TradeMarkType> trademarkToFileDetailsConverter(JAXBSerializer trademarkJaxbSerializer) {
        return new JAXBObjectToFileDetailsConverter<>(
                new FileDetailsCreator<TradeMarkType>(trademarkJaxbSerializer),
                (trademark, fileNameDatePrefix) -> {
                    StringBuilder fileName = new StringBuilder()
                            .append(trademark.getApplicationNumber())//.replace(" ", "-"))
                            .append(FILE_EXTENSION);
                    return fileName.toString();
                });
    }

    @Bean(name = "designToFileDetailsConverter")
    public JAXBObjectToFileDetailsConverter<DesignType> designToFileDetailsConverter(JAXBSerializer designJaxbSerializer) {
        return new JAXBObjectToFileDetailsConverter<>(
                new FileDetailsCreator<TradeMarkType>(designJaxbSerializer),
                (design, fileNameDatePrefix) -> {
                    StringBuilder fileName = new StringBuilder()
                            .append(design.getApplicationNumber())//.replace(" ", "-"))
                            .append("-")
                            .append(design.getDesignIdentifier())//.replace(" ", "-"))
                            .append(FILE_EXTENSION);

                    return fileName.toString();
                }
        );
    }

}
