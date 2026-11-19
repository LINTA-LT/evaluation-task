package lt.gov.vpb.pdb.springbatch.opendata.ds.converters;

import lombok.extern.slf4j.Slf4j;
import lt.vpb.open_data.schemas.address.v1_1.AddressType;
import lt.vpb.open_data.schemas.address.v1_1.AddressType.FormattedNameAddress;
import lt.vpb.open_data.schemas.address.v1_1.AddressType.FormattedNameAddress.Address.FormattedAddress;
import lt.vpb.open_data.schemas.design.v1_2.DesignType.ApplicantDetails;
import lt.vpb.open_data.schemas.design.v1_2.DesignType.ApplicantDetails.Applicant;
import lt.vpb.open_data.schemas.design.v1_2.DesignType.RepresentativeDetails;
import lt.vpb.open_data.schemas.design.v1_2.DesignType.RepresentativeDetails.Representative;
import org.springframework.stereotype.Component;
import org.tmdn.cti.repository.jpa.domain.design.Address;
import org.tmdn.cti.repository.jpa.domain.design.Design;
import org.tmdn.cti.repository.jpa.domain.design.DesignPersonAddress;
import org.tmdn.cti.repository.jpa.domain.design.Person;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

@Component
@Slf4j
public class PersonConverter {

    public RepresentativeDetails toRepresentativeDetails(Design design) {

        RepresentativeDetails representativeDetails = new RepresentativeDetails();

        for (Person person : design.getRepresentatives()) {
            Representative representative = new Representative();
            representative.setPersonType(person.getLegalEntity());
            AddressType addressType = new AddressType();
            addressType.setFormattedNameAddress(toFormattedNameAndAddress(design, person, design.getRepresentativeAddresses()));
            representative.setAddressBook(addressType);
            representativeDetails.getRepresentative().add(representative);
        }
        return representativeDetails;
    }


    public ApplicantDetails toApplicantDetails(Design design) {

        ApplicantDetails applicantDetails = new ApplicantDetails();

        for (Person person : design.getApplicants()) {
            Applicant applicant = new Applicant();
            applicant.setPersonType(person.getLegalEntity());
            AddressType addressType = new AddressType();
            addressType.setFormattedNameAddress(toFormattedNameAndAddress(design, person, design.getApplicantAddresses()));
            applicant.setAddressBook(addressType);
            applicantDetails.getApplicant().add(applicant);
        }
        return applicantDetails;
    }


    public FormattedNameAddress toFormattedNameAndAddress(Design design, Person person, List<DesignPersonAddress> addresses) {

        FormattedNameAddress formattedNameAddress = new FormattedNameAddress();

        if (isEmpty(addresses)) {
            log.debug("No Addresses found for design id={}, applicationNumber={}, person id={} {}",
                    design.getId(), design.getApplicationNumber(), person.getId(), person.getLegalEntity());
            return formattedNameAddress;
        }
        Address jpaAddress = addresses.get(0).getAddress();

        FormattedNameAddress.Address address = new FormattedNameAddress.Address();
        address.setAddressCountryCode(jpaAddress.getCountryCode());
        if (person.isLegalEntity()) {
            formattedNameAddress.setOrganizationName(person.getName().getOrganizationName());
            FormattedAddress formattedAddress = new FormattedAddress();
            formattedAddress.setAddressCity(jpaAddress.getCity());
            formattedAddress.setAddressState(jpaAddress.getState());
            formattedAddress.setAddressStreet(jpaAddress.getStreet());
            formattedAddress.setAddressPostcode(jpaAddress.getPostCode());
            address.setFormattedAddress(formattedAddress);
        }
        formattedNameAddress.getAddress().add(address);
        return formattedNameAddress;
    }
}

