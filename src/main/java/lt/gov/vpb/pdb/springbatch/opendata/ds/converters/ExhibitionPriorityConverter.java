package lt.gov.vpb.pdb.springbatch.opendata.ds.converters;


import lt.vpb.open_data.schemas.design.v1_2.DesignType.ExhibitionPriorityDetails;
import lt.vpb.open_data.schemas.design.v1_2.ExhibitionPriorityType;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;
import org.tmdn.cti.repository.jpa.converter.AbstractConverter;
import org.tmdn.cti.repository.jpa.domain.design.DesignExhibitionPriority;

import java.util.List;

@Component
public class ExhibitionPriorityConverter extends AbstractConverter<DesignExhibitionPriority, ExhibitionPriorityType, ExhibitionPriorityDetails> {

	@Override protected ExhibitionPriorityDetails wrap(List<ExhibitionPriorityType> list) {
        return new ExhibitionPriorityDetails().withExhibitionPriority(list);
    }

    @Override public ExhibitionPriorityType convert(DesignExhibitionPriority object) {
    	ExhibitionPriorityType exhibition = new ExhibitionPriorityType();
    	if(object.getExhibitionDate()!= null) {
        	exhibition.withExhibitionDate(object.getExhibitionDate());	
    	}
    	if(object.getExhibitionFirstDisplayDate() != null) {
    		exhibition.withExhibitionFirstDisplayDate(LocalDate.fromDateFields(object.getExhibitionFirstDisplayDate()));
    	}
        return exhibition;
    }
}
