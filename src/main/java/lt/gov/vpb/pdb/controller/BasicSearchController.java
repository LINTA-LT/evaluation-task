package lt.gov.vpb.pdb.controller;

import lt.gov.vpb.pdb.controller.util.ConstantUtils;
import lt.gov.vpb.pdb.repository.jpa.patent.ReferenceDataJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDate;

public class BasicSearchController {

    @Autowired
    @Qualifier("referenceDataJpaRepository")
    ReferenceDataJpaRepository referenceDataRepository;

    private static final String LT = "lt";
    private static final String EN = "en";

    @ModelAttribute
    public void addAttributes(Model model) {
        String locale = LocaleContextHolder.getLocale().getLanguage();
        //for elastic search databases
        model.addAttribute("lastUpdated", LocalDate.now().minusDays(1L));
        //common date format
        model.addAttribute("dateFormat", "yyyy-MM-dd");
        //for countries selection
        model.addAttribute("countriesLt", referenceDataRepository.getCountriesLt());
        model.addAttribute("countriesEn", referenceDataRepository.getCountriesEn());
        if (LT.equals(locale)) {
            model.addAttribute("patentEuStatuses", referenceDataRepository.getPatentEuStatusesLt());
            model.addAttribute("statusLtList", ConstantUtils.STATUS_LT_EN.keySet());
            model.addAttribute("patentStatuses", referenceDataRepository.getPatentStatusesLt());
        } else if (EN.equals(locale)) {
            model.addAttribute("patentEuStatuses", referenceDataRepository.getPatentEuStatusesEn());
            model.addAttribute("statusEnList", ConstantUtils.STATUS_LT_EN.values());
            model.addAttribute("patentStatuses", referenceDataRepository.getPatentStatusesEn());
        } else {
            model.addAttribute("patentEuStatuses", referenceDataRepository.getPatentEuStatusesLt());
            model.addAttribute("statusLtList", ConstantUtils.STATUS_LT_EN.keySet());
            model.addAttribute("countriesLt", referenceDataRepository.getCountriesLt());
        }
    }

}
