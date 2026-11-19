package lt.gov.vpb.pdb.springbatch.opendata.ds.converters;

import lt.vpb.open_data.schemas.design.v1_2.DesignBasicRecordType;
import lt.vpb.open_data.schemas.design.v1_2.DesignRecordType;
import lt.vpb.open_data.schemas.design.v1_2.DesignType;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;
import org.tmdn.cti.repository.jpa.converter.AbstractConverter;
import org.tmdn.cti.repository.jpa.domain.design.DesignRecord;

import java.util.List;

@Component
public class RecordConverter extends AbstractConverter<DesignRecord, DesignRecordType, DesignType.DesignRecordDetails> {

    @Override
    protected DesignType.DesignRecordDetails wrap(List<DesignRecordType> list) {
        return new DesignType.DesignRecordDetails().withDesignRecord(list);
    }

    @Override
    public DesignRecordType convert(DesignRecord record) {
        DesignBasicRecordType recordType = new DesignBasicRecordType();

        if (!StringUtils.isBlank(record.getIdentifier())) {
            recordType.withRecordIdentifier(record.getIdentifier());
        }
        if (record.getFilingDate() != null) {
            recordType.withRecordFilingDate(record.getFilingDate());
        }
        recordType.withBasicRecordKind(record.getBasicRecordKind());
        if (!StringUtils.isBlank(record.getRecordReference())) {
            recordType.withRecordReference(record.getRecordReference());
        }
        recordType.withRecordCurrentStatusCode(record.getStatusCode());

        if(record.getStatusDate() != null) {
            recordType.withRecordCurrentStatusDate(LocalDate.fromDateFields(record.getStatusDate()));
        }
        if (record.getPublicationDate() != null) {
            recordType.withRecordPublicationDate(record.getPublicationDate());
        }
        if (record.getNotificationDate() != null) {
            recordType.withRecordNotificationDate(record.getNotificationDate());
        }
        if (record.getEffectiveDate() != null) {
            recordType.withRecordEffectiveDate(record.getEffectiveDate());
        }

        return new DesignRecordType().withDesignBasicRecord(recordType);
    }
}
