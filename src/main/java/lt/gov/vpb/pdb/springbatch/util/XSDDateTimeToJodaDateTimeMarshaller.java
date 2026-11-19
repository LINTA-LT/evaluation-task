package lt.gov.vpb.pdb.springbatch.util;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

@Slf4j
public class XSDDateTimeToJodaDateTimeMarshaller {

    private XSDDateTimeToJodaDateTimeMarshaller() {
    }

    public static DateTime unmarshal(String xmlGregorianCalendar) {
        return new DateTime(xmlGregorianCalendar);
    }

    public static String marshal(DateTime dateTime) {
        String result = "MARSHALLING_ERROR";
        try {
            result = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateTime.toGregorianCalendar()).toXMLFormat();
        } catch (DatatypeConfigurationException e) {
            log.error("Error marshalling Joda DateTime to xmlGregorianCalendar", e);
        }
        return result;
    }
}
