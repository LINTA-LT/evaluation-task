package lt.gov.vpb.pdb.springbatch.util;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class XSDDateToJodaLocalDateMarshaller {

    private XSDDateToJodaLocalDateMarshaller() {
    }

    public static LocalDate unmarshal(String xmlGregorianCalendar) {
        return new LocalDate(xmlGregorianCalendar);
    }

    public static String marshal(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");

        return formatter.print(localDate);
    }
}
