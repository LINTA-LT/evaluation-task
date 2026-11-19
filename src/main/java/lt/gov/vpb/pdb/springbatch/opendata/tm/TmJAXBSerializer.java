package lt.gov.vpb.pdb.springbatch.opendata.tm;

import lt.gov.vpb.pdb.springbatch.util.JAXBSerializer;
import lt.vpb.open_data.schemas.trademark.v1_2.ObjectFactory;
import lt.vpb.open_data.schemas.trademark.v1_2.TradeMarkType;

import javax.xml.bind.JAXBElement;

public class TmJAXBSerializer extends JAXBSerializer<TradeMarkType> {
	
	public TmJAXBSerializer(String schemaFileName) {
		super(schemaFileName);
	}

	@Override
	public JAXBElement<TradeMarkType> createJAXBElement(Object object) {

		ObjectFactory objectFactory = new ObjectFactory();
		JAXBElement<TradeMarkType> jaxbElement = objectFactory.createTradeMark((TradeMarkType)object);
		return jaxbElement;
	}

}
