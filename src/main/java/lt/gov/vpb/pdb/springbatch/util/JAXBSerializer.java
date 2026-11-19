package lt.gov.vpb.pdb.springbatch.util;

import lombok.extern.slf4j.Slf4j;
import lt.gov.vpb.pdb.springbatch.opendata.file.ValidationResult;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.util.Assert;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;

/**
 * Serializes objects to XML using JAXB and puts it into byte array.
 * Used for creating XML files.
 */
@Slf4j
public abstract class JAXBSerializer<T> {
    
	private String schemaFileName;
    
	protected ValidationResult result;
    
    public JAXBSerializer() {
    	result = new ValidationResult();
    }

    public JAXBSerializer(String schemaFileName) {
        this.schemaFileName = schemaFileName;
        result = new ValidationResult();
    }

    public byte[] serialize(Object object) {
    	result = new ValidationResult();
        Assert.notNull(object, "object cannot be null");
        JAXBInitiator jaxBInitiator = null;
        try {
        	jaxBInitiator = new JAXBInitiator(object.getClass(), schemaFileName);
			Marshaller marshaller = jaxBInitiator.getMarshaller();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            marshaller.marshal(createJAXBElement(object), byteArrayOutputStream);
            result.setFailed(false);
            return byteArrayOutputStream.toByteArray();
        } catch (JAXBException e) {
        	result.setFailed(true);
        	result.setErrorMessage(jaxBInitiator.getErrorDescription());
        	log.error("Error serializing the object: {}", ToStringBuilder.reflectionToString(object));
        	log.error(jaxBInitiator.getErrorDescription(), e);
        	return new byte[0];
        }
    }

	public ValidationResult getResult() {
		return result;
	}

	public void setResult(ValidationResult result) {
		this.result = result;
	}

	public abstract JAXBElement<T> createJAXBElement(Object object);
}
