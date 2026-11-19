package lt.gov.vpb.pdb.repository.jpa.patent;

import lt.gov.vpb.pdb.model.util.OptionValue;
import lt.gov.vpb.pdb.repository.jpa.patent.dao.AuditLogDAO;
import lt.gov.vpb.pdb.repository.jpa.patent.dao.CountryDAO;
import lt.gov.vpb.pdb.repository.jpa.patent.dao.EuDossierDAO;
import lt.gov.vpb.pdb.repository.jpa.patent.dao.LtDossierDAO;
import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(transactionManager = "pdbpatentsTransactionManager", readOnly = true)
public class ReferenceDataJpaRepository {

    @Autowired
    @Qualifier("countryDAO")
    CountryDAO countryDAO;

    @Autowired
    @Qualifier("auditLogDAO")
    AuditLogDAO auditLogDAO;

    @Autowired
    EuDossierDAO euDossierDAO;

    @Autowired
    LtDossierDAO ltDossierDAO;

    //Cache time-to-live (1 hour)
    private static final long ttl = 3600000L;
    private static final String COUNTRIES_LT_KEY = "countriesLt";
    private static final String COUNTRIES_EN_KEY = "countriesEn";
    private static final String PATENT_EU_STATUSES_LT = "patentEuStatusesLt";
    private static final String PATENT_EU_STATUSES_EN = "patentEuStatusesEn";
    private static final String PATENT_STATUSES_LT = "patentStatusesLt";
    private static final String PATENT_STATUSES_EN = "patentStatusesEn";

    PassiveExpiringMap<String, Collection<OptionValue>> options = new PassiveExpiringMap<>(new PassiveExpiringMap.ConstantTimeToLiveExpirationPolicy(ttl));

    public Collection<OptionValue> getCountriesLt() {
        if(!options.containsKey(COUNTRIES_LT_KEY)){
            options.put(COUNTRIES_LT_KEY,
                    countryDAO.findAllByOrderByNameLtAsc().stream()
                    .map(country -> new OptionValue(country.getIso2(), country.getNameLt()))
                    .collect(Collectors.toList())
            );
        }
        return options.get(COUNTRIES_LT_KEY);
    }

    public Collection<OptionValue> getCountriesEn() {
        if(!options.containsKey(COUNTRIES_EN_KEY)){
            options.put(COUNTRIES_EN_KEY,
                    countryDAO.findAllByOrderByNameEnAsc().stream()
                            .map(country -> new OptionValue(country.getIso2(), country.getNameEn()))
                            .collect(Collectors.toList())
            );
        }
        return options.get(COUNTRIES_EN_KEY);
    }

    public Collection<OptionValue> getPatentEuStatusesLt() {
        if(!options.containsKey(PATENT_EU_STATUSES_LT)){
            List<OptionValue> statusOptions = euDossierDAO.getPatentEuStatuses().stream()
                    .map(status -> new OptionValue(status.getCombinedStatusIds(), status.getDescriptionLt()))
                    .collect(Collectors.toList());

            statusOptions.add(new OptionValue("EP_VERTIMAS_NEBUVO_PADUOTAS", "patent.eu.details.status.EP_VERTIMAS_NEBUVO_PADUOTAS_2"));
            statusOptions.add(new OptionValue("EP_EFFECTS_OF_THE_PATENT_ARE_LOST_AB_INITIO", "patent.eu.details.status.EP_EFFECTS_OF_THE_PATENT_ARE_LOST_AB_INITIO_2"));

            options.put(PATENT_EU_STATUSES_LT, statusOptions);
        }
        return options.get(PATENT_EU_STATUSES_LT);
    }

    public Collection<OptionValue> getPatentEuStatusesEn() {
        if(!options.containsKey(PATENT_EU_STATUSES_EN)){
            List<OptionValue> statusOptions = euDossierDAO.getPatentEuStatuses().stream()
                    .map(status -> new OptionValue(status.getCombinedStatusIds(), status.getDescriptionEn()))
                    .collect(Collectors.toList());

            statusOptions.add(new OptionValue("EP_VERTIMAS_NEBUVO_PADUOTAS", "patent.eu.details.status.EP_VERTIMAS_NEBUVO_PADUOTAS_2"));
            statusOptions.add(new OptionValue("EP_EFFECTS_OF_THE_PATENT_ARE_LOST_AB_INITIO", "patent.eu.details.status.EP_EFFECTS_OF_THE_PATENT_ARE_LOST_AB_INITIO_2"));

            options.put(PATENT_EU_STATUSES_EN, statusOptions);
        }
        return options.get(PATENT_EU_STATUSES_EN);
    }

    public Collection<OptionValue> getPatentStatusesLt() {
        if(!options.containsKey(PATENT_STATUSES_LT)){
            List<OptionValue> statusOptions = ltDossierDAO.getPatentStatuses().stream()
                    .map(status -> new OptionValue(status.getCombinedStatusIds(), status.getDescriptionLt()))
                    .collect(Collectors.toList());
            options.put(PATENT_STATUSES_LT, statusOptions);
        }
        return options.get(PATENT_STATUSES_LT);
    }

    public Collection<OptionValue> getPatentStatusesEn() {
        if(!options.containsKey(PATENT_STATUSES_EN)){
            List<OptionValue> statusOptions = ltDossierDAO.getPatentStatuses().stream()
                    .map(status -> new OptionValue(status.getCombinedStatusIds(), status.getDescriptionEn()))
                    .collect(Collectors.toList());
            options.put(PATENT_STATUSES_EN, statusOptions);
        }
        return options.get(PATENT_STATUSES_EN);
    }

    public Optional<LocalDateTime> getLastAuditLog(String type){
        return auditLogDAO.findAllByTypeOrderByCreatedAtDesc(type).stream().findFirst().map(a -> a.getCreatedAt());
    }

}
