package lt.gov.vpb.pdb.springbatch.opendata.ds.converters;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.tmdn.cti.repository.jpa.domain.design.Design;

@Component
@Slf4j
public class ST13Generator {

    private static final String DESIGN_CODE = "70";

    /***
     * a. For years > 2000
     *   [YYYY](space)[3digit number]
     * b. For years 95 - 99
     *   [YY](-)[3digit number]
     * c. International registrations
	 *   H[YYYY](space)[3digit number]
 	 * d. Other National Formats  
 	 *   PP[3digit number]
 	 *   RP[3digit number]
 	 * e. Divisional design applications 
 	 *   [YYYY](space)[3digit number][letter] 

     * @param design
     * @return
     */
    
    public String forDesign(Design design) {
    	//LT specific
    	//if applicationNumber startsWith [YY] or [YYYY] then get the year from the applicationNumber, orelse get it from application date
    	String year;
		if(design.getApplicationNumber().startsWith("9")){
			year = "19" + design.getApplicationNumber().substring(0, 2);
		} else if (design.getApplicationNumber().startsWith("2")){
			year = design.getApplicationNumber().substring(0,4);
		} else {
			year = String.valueOf(design.getApplicationDate().getYear());
		}

    	String applicationNumber;
    	/** Cases a,c,e */
    	if(design.getApplicationNumber().contains(" ")) {
    		applicationNumber = design.getApplicationNumber().substring(design.getApplicationNumber().indexOf(' ')+1);
    	} /** Case b*/ 
    	else if (design.getApplicationNumber().contains("-")) {
    		applicationNumber = design.getApplicationNumber().substring(design.getApplicationNumber().indexOf('-')+1);
    	}  /** Case d*/ 
    	else {
    		applicationNumber = design.getApplicationNumber();
    	}
    	String st13 = design.getRegistrationOfficeCode() + DESIGN_CODE +  year + StringUtils.leftPad(applicationNumber, 9, "0");
        
    	log.debug("ST13 for design is {}", st13);

        st13 = st13 + "-" + StringUtils.leftPad(design.getDesignIdentifier(), 4, "0");

        return st13;
    }
    
}
