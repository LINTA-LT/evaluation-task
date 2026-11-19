package lt.gov.vpb.pdb.springbatch.opendata.tm.converters;

import lt.vpb.open_data.schemas.trademark.v1_2.KindMarkType;

/**
 * LT specific. Custom converter for legacy types (as a workaround in relation to AVP-1034)
 */
public class KindMarkTypeConverter {

    public static final String LEGACY_KINDMARKTYPE_CERTIFICATION = "Certification";

    public static KindMarkType convert(String ipKind) {
        if (LEGACY_KINDMARKTYPE_CERTIFICATION.equals(ipKind)) {
            return KindMarkType.CERTIFICATE;
        } else {
            return KindMarkType.fromValue(ipKind);
        }
    }

}
