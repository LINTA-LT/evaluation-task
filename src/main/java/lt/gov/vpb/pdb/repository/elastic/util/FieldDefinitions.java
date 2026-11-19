package lt.gov.vpb.pdb.repository.elastic.util;

public class FieldDefinitions {

    private FieldDefinitions() {
    }

    public static class DesignFields {
        public static final String APPLICATION_NUMBER = "applicationnumber";
        public static final String REGISTRATION_NUMBER = "registrationnumber";
        public static final String APPLICATION_DATE = "dtapplication";
        public static final String EXPIRY_DATE = "dtexpiry";
        public static final String REGISTRATION_DATE = "dtregistration";
        public static final String PUBLICATION_DATE = "dtpublication";
        public static final String DESCRIPTION = "description";
        public static final String ST13 = "st13";
        public static final String LOCARNO_CLASSIFICATION = "locarnoclassification";
        public static final String STATUS_LT = "status_lt";
        public static final String STATUS_EN = "status_en";
        public static final String INDICATION_RIGHTS_PLEDGED = "indrightspledged";
        public static final String INDICATION_RIGHTS_ARRESTED = "indrightsarrested";
        public static final String DESIGN_IDENTIFIERS = "designidentifiers";
        public static final String SAMPLES = "samples";
        public static final String REGISTRATION_PUBLICATION_IDENTIFIER = "regpubidentifier";

        public static final String DESIGN_IDENTIFIER = "designidentifier";
        public static final String SEQUENCE_NUMBER = "sequencenumber";
        public static final String APPLICANTS = "applicants";
        public static final String REPRESENTATIVES = "representatives";
        public static final String CONVENTION_PRIORITIES = "dsPriorities";

        private DesignFields() {
        }
    }

    public static class PersonFields {
        public static final String FREEFORM_NAME = "freeformname";
        public static final String FREEFORM_ADDRESS = "freeformaddress";
        public static final String ORG_NAME = "orgname";
        public static final String FIRST_NAME = "firstname";
        public static final String MIDDLE_NAME = "middlename";
        public static final String LAST_NAME = "lastname";
        public static final String STREET = "street";
        public static final String POSTAL_CODE = "postcode";
        public static final String CITY = "city";
        public static final String COUNTRY_CODE = "tycountrycode";
        public static final String SANCTIONS = "sanctions";

        public static final String ISDEFAULT = "isdefault";
        public static final String ISPREMISES = "ispremises";
        public static final String ISPRIMARY = "isprimary";

        private PersonFields() {
        }
    }

    public static class TrademarkFields {
        public static final String DESCRIPTION = "description";
        public static final String APPLICATION_NUMBER = "applicationnumber";
        public static final String REGISTRATION_NUMBER = "registrationnumber";
        public static final String REGISTRATION_NUMBER_NUMERIC = "registrationnumber_numeric";
        public static final String APPLICATION_DATE = "dtapplication";
        public static final String EXPIRY_DATE = "dtexpiry";
        public static final String REGISTRATION_DATE = "dtregistration";
        public static final String PUBLICATION_DATE = "dtpublication";
        public static final String APPLICATION_PUBLICATION_IDENTIFIER = "apppubidentifier";
        public static final String APPLICATION_PUBLICATION_DATE = "apppubdate";
        public static final String REGISTRATION_PUBLICATION_IDENTIFIER = "regpubidentifier";
        public static final String REGISTRATION_PUBLICATION_DATE = "regpubdate";
        public static final String CLASSIFICATION = "classification";
        public static final String VIENNA_CATEGORY_CODE = "viennacategorycode";
        public static final String NICECLASSNUMBER = "niceclassnumber";
        public static final String CLASSIFICATION_DESCRIPTION = "classification.description";
        public static final String CLASSIFICATION_NICECLASSNUMBER = "classification.niceclassnumber";
        public static final String APPLICANTS = "applicants";
        public static final String REPRESENTATIVES = "representatives";
        public static final String COLOUR_CLAIM_TEXT = "colourclaimtext";

        public static final String DISCLAIMER = "disclaimer";
        public static final String INDICATION_SENIORITY = "indseniority";
        public static final String INDICATION_RIGHTS_PLEDGED = "indrightspledged";
        public static final String INDICATION_RIGHTS_ARRESTED = "indrightsarrested";
        public static final String CTM_SENIORITY = "ctmseniority";
        public static final String MULTIMEDIA_FILENAME = "multimediafilename";
        public static final String SOUND_FILENAME = "soundfilename";
        public static final String IMAGE_FILENAME = "imagefilename";
        public static final String EXHIBITION_PRIORITIES = "exhibitionPriorities";

        public static final String PRIORITIES = "priorities";
        public static final String PRIORITIES_SU = "prioritiesSovietUnion";
        public static final String PRIORITIES_EARLIER = "earlierPriorities";
        public static final String TYFEATURE = "tyfeature";
        public static final String TYKIND = "tykind";
        public static final String STATUS_LT = "status_lt";
        public static final String STATUS_EN = "status_en";
        public static final String ST13 = "st13";
        public static final String TYFEATURE_MULTIMEDIA = "Multimedia";
        public static final String TYFEATURE_SOUND = "Sound";

        private TrademarkFields() {
        }
    }

    public static class NiceClassificationFields {
        public static final String CLASS_NUMBER = "class_number";
        public static final String TERM_NUMBER = "term_number";
        public static final String TERM_LT = "term_lt";
        public static final String TERM_EN = "term_en";
        public static final String BASIC_NUMER = "basic_number";
        public static final String VERSION_YEAR = "year";
        public static final String STATUS = "status";

        private NiceClassificationFields() {
        }
    }

    public static class DisputeFields {
        public static final String APPLICATION_DATE = "dtapplicationdate";
        public static final String DISPUTE_TYPE = "disputetype";
        public static final String REF_APPLICATION_NUMBER = "refapplicationnumber";
        public static final String DECISION_EFFECTIVE_DATE = "dtdecisioncameintoeffect";
        public static final String CASE_NUMBER = "nrcasenumber";
        public static final String VERBAL_ELEMENT = "dscverbalelement";
        public static final String APPLICATION_NUMBER = "applicationnumber";
        public static final String REGISTRATION_NUMBER = "registrationnumber";
        public static final String NICE_CLASSIFICATION = "niceclassification";
        public static final String GROUNDS = "grounds";
        public static final String DECISION_TAKEN = "tydecisiontaken";
        public static final String COMMENTS = "comments";
        public static final String IND_INTERNATIONAL = "indinternational";
        public static final String CURRENT_STATUS = "stcurrentstatus";

        private DisputeFields() {
        }
    }

    public static class PriorityFields {
        public static final String PRIORITY_NUMBER = "prioritynumber";
        public static final String COUNTRY_CODE = "tycountrycode";
        public static final String PRIORITY_DATE = "dtpriority";
        public static final String BULLETIN_NR = "bulletinnr";

        private PriorityFields() {
        }
    }

    public static class ExibitionPriorityFields {
        public static final String COUNTRY_CODE = "tycountrycode";
        public static final String PRIORITY_DATE = "dtpriority";

        private ExibitionPriorityFields() {
        }
    }


}
