package lt.gov.vpb.pdb.springbatch.opendata.tm.converters;

import lombok.extern.slf4j.Slf4j;
import lt.vpb.open_data.schemas.address.v1_1.AddressType;
import lt.vpb.open_data.schemas.address.v1_1.AddressType.FormattedNameAddress;
import lt.vpb.open_data.schemas.address.v1_1.AddressType.FormattedNameAddress.Address;
import lt.vpb.open_data.schemas.address.v1_1.AddressType.FormattedNameAddress.Address.FormattedAddress;
import lt.vpb.open_data.schemas.trademark.v1_2.TradeMarkType.ApplicantDetails;
import lt.vpb.open_data.schemas.trademark.v1_2.TradeMarkType.ApplicantDetails.Applicant;
import lt.vpb.open_data.schemas.trademark.v1_2.TradeMarkType.RepresentativeDetails;
import lt.vpb.open_data.schemas.trademark.v1_2.TradeMarkType.RepresentativeDetails.Representative;
import org.springframework.stereotype.Component;
import org.tmdn.cti.repository.jpa.domain.trademark.MarkPersonAddress;
import org.tmdn.cti.repository.jpa.domain.trademark.Person;
import org.tmdn.cti.repository.jpa.domain.trademark.TradeMark;

import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

@Component("opendata.tm.personConverter")
@Slf4j
public class PersonConverter {

    public RepresentativeDetails convertRepresentatives(TradeMark trademark) {

        RepresentativeDetails representativeDetails = new RepresentativeDetails();

        for (Person person : trademark.getRepresentatives()) {
            Representative representative = new Representative();
            representative.setPersonType(person.getLegalEntity());
            AddressType addressType = new AddressType();
            addressType.setFormattedNameAddress(toFormattedNameAndAddress(trademark, person, trademark.getRepresentativeAddresses()));
            representative.setAddressBook(addressType);
            representativeDetails.getRepresentative().add(representative);
        }
        return representativeDetails;
    }


    public ApplicantDetails convertApplicants(TradeMark trademark) {

        ApplicantDetails applicantDetails = new ApplicantDetails();

        for (Person person : trademark.getApplicants()) {
            Applicant applicant = new Applicant();
            applicant.setPersonType(person.getLegalEntity());
            AddressType addressType = new AddressType();
            addressType.setFormattedNameAddress(toFormattedNameAndAddress(trademark, person, trademark.getApplicantAddresses()));
            applicant.setAddressBook(addressType);
            applicantDetails.getApplicant().add(applicant);
        }
        return applicantDetails;
    }


    public FormattedNameAddress toFormattedNameAndAddress(TradeMark trademark, Person person, List<MarkPersonAddress> addresses) {

        FormattedNameAddress formattedNameAddress = new FormattedNameAddress();

        if (isEmpty(addresses)) {
            log.debug("No Addresses found for trademark id={}, applicationNumber={}, person id={} {}",
                    trademark.getId(), trademark.getApplicationNumber(), person.getId(), person.getLegalEntity());
            return formattedNameAddress;
        }
        org.tmdn.cti.repository.jpa.domain.trademark.Address jpaAddress = addresses.get(0).getAddress();

        Address address = new Address();
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
