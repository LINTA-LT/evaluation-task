package lt.gov.vpb.pdb.springbatch.opendata.ds;

import lt.gov.vpb.pdb.springbatch.util.JAXBSerializer;
import lt.vpb.open_data.schemas.design.v1_2.DesignType;
import lt.vpb.open_data.schemas.design.v1_2.ObjectFactory;

import javax.xml.bind.JAXBElement;

public class DesignJAXBSerializer extends JAXBSerializer<DesignType> {
	
	public DesignJAXBSerializer(String schemaFileName) {
		super(schemaFileName);
	}

	@Override
	public JAXBElement<DesignType> createJAXBElement(Object object) {
		ObjectFactory objectFactory = new ObjectFactory();
		JAXBElement<DesignType> jaxbElement = objectFactory.createDesign((DesignType)object);
		return jaxbElement;
	}

}
