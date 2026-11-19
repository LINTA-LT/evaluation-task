package lt.gov.vpb.pdb.repository.elastic.util.converter;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import static java.time.ZoneId.systemDefault;

@Slf4j
public class StringToLocalDateConverter {

    public static LocalDate convert(Object dateTimeString) {
        if (dateTimeString == null || !(dateTimeString instanceof String)) {
            return null;
        }
        try {
            return ZonedDateTime.parse((String) dateTimeString).withZoneSameInstant(systemDefault()).toLocalDate();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
