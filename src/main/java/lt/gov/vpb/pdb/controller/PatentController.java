package lt.gov.vpb.pdb.controller;

import lombok.extern.slf4j.Slf4j;
import lt.gov.vpb.pdb.controller.util.DocumentUtils;
import lt.gov.vpb.pdb.form.patent.*;
import lt.gov.vpb.pdb.model.PaginatedList;
import lt.gov.vpb.pdb.model.Person;
import lt.gov.vpb.pdb.model.patent.*;
import lt.gov.vpb.pdb.repository.jpa.patent.DocumentJpaRepository;
import lt.gov.vpb.pdb.repository.jpa.patent.DossierJpaRepository;
import lt.gov.vpb.pdb.repository.util.PatentImageUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.Locale;

import static java.io.File.separatorChar;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Controller
@Slf4j
@RequestMapping("/patent")
public class PatentController extends BasicSearchController {

    @Value("${patent.images-dir}")
    private String directory;

    @Autowired
    PatentImageUtil patentImageUtil;

    @Autowired
    DossierJpaRepository dossierRepository;

    @Autowired
    DocumentJpaRepository documentRepository;

    @Autowired
    MessageSource messageSource;

    @ModelAttribute
    @Override
    public void addAttributes(Model model) {
        super.addAttributes(model);
        model.addAttribute("lastUpdated", referenceDataRepository.getLastAuditLog("patents")
                .map(LocalDate::from)
                .orElse(LocalDate.now()));
        //false as default, otherwise the value will be explicitly set
        model.addAttribute("isSpaResults", FALSE);
    }

    @GetMapping(path = {"/{type}/search-form"})
    public String searchForm(@PathVariable String type, Model model, LtDossierSearchForm searchForm) {
        model.addAttribute("searchForm", searchForm);
        return type + "-patent-search-form";
    }

    @GetMapping(path = {"/{type}/advanced-search-form"})
    public String advancedSearchForm(@PathVariable String type, Model model, LtDossierAdvancedSearchForm searchForm) {
        model.addAttribute("searchForm", searchForm);
        return type + "-patent-advanced-search-form";
    }

    @GetMapping(path = {"/{type}/text-search-form"})
    public String searchForm(Model model, LtDossierSearchForm searchForm) {
        model.addAttribute("searchForm", searchForm);
        return "lt-patent-text-search-form";
    }

    @GetMapping(path = "/lt/search")
    public String searchLt(Model model, LtDossierSearchForm searchForm) {
        PaginatedList<DossierListItem> searchResponse = dossierRepository.ltDossierSearch(searchForm);
        model.addAttribute("searchForm", searchForm);
        model.addAttribute("totalHits", searchResponse.getTotalItems());
        model.addAttribute("dossiers", searchResponse.getData());
        return "lt-patent-search-results";
    }

    @GetMapping(path = "/lt/text-search")
    public String searchLtText(Model model, LtDossierTextSearchForm searchForm) {
        searchLt(model, searchForm);
        return "lt-patent-text-search-results";
    }

    @GetMapping(path = "/lt/advanced-search")
    public String searchLtAdvanced(Model model, LtDossierAdvancedSearchForm searchForm) {
        searchLt(model, searchForm);
        model.addAttribute("isSpaResults", !isEmpty(searchForm.getSpa()) || !isEmpty(searchForm.getSpa_f95()) ? TRUE : FALSE);
        return "lt-patent-search-results";
    }

    @GetMapping(path = "/eu/search")
    public String searchEu(Model model, EuDossierSearchForm searchForm) {
        PaginatedList<DossierListItem> searchResponse = dossierRepository.euDossierSearch(searchForm);
        model.addAttribute("searchForm", searchForm);
        model.addAttribute("totalHits", searchResponse.getTotalItems());
        model.addAttribute("patents", searchResponse.getData());
        return "eu-patent-search-results";
    }

    @GetMapping(path = "/eu/advanced-search")
    public String searchEuAdvanced(Model model, EuDossierAdvancedSearchForm searchForm) {
        searchEu(model, searchForm);
        model.addAttribute("isSpaResults", !isEmpty(searchForm.getSpa()) || !isEmpty(searchForm.getSpa_f95()) ? TRUE : FALSE);
        return "eu-patent-search-results";
    }

    @GetMapping(path = "/dossier/{number}")
    public String details(@PathVariable String number, Model model) {
        Dossier dossier = dossierRepository.findLtDossierDetails(number);
        if(dossier == null){
            return "not-found";
        }
        if(dossier instanceof  EuPatent) {
            EuDossier euDossier = dossierRepository.findEuDossierDetailsByApplicationNumber(number);
            model.addAttribute("dossier", euDossier);
            return "eu-patent-details";
        } else if(dossier instanceof LtPatent){
            LtPatent patent = (LtPatent)dossier;
            model.addAttribute("patent", patent);
            return "lt-patent-details";
        } else if (dossier instanceof SPA){
            SPA spa = (SPA)dossier;
            model.addAttribute("spa", spa);
            return "patent-spa-details";
        } else {
            throw new RuntimeException(format("Dossier type %s not recognized", dossier.getClass()));
        }
    }

    @GetMapping(path = "/eu/dossier/{number}")
    public String euDossierDetails(@PathVariable String number, Model model) {
        EuDossier dossier = dossierRepository.findEuDossierDetailsByPatentNumber(number);
        if(dossier == null){
            return "not-found";
        }
        model.addAttribute("dossier", dossier);
        return "eu-patent-details";
    }

    @GetMapping(path = "/dossier/{number}/text")
    @ResponseBody
    public String detailsText(@PathVariable String number) throws IOException {
        return IOUtils.toString(new FileReader(directory + separatorChar + number + separatorChar + "index.html"));
    }

    @GetMapping(path = "/dossier/{number}/licence/list")
    public String licences(@PathVariable String number, Model model) {
        LicencesList licencesList = dossierRepository.findDossierLicences(number);
        if(licencesList == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        model.addAttribute("dossier", licencesList);
        return "patent-licences-list";
    }

    @GetMapping(path = "/dossier/{number}/{imageFilename}")
    @ResponseBody
    public ResponseEntity<byte[]> image(@PathVariable String number, @PathVariable String imageFilename) {
        byte[] bytes = patentImageUtil.getJpegImage(number, imageFilename);
        if(bytes == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @GetMapping(path = {"/dossier/{number}/document/{documentId}", "/dossier/document/{documentId}"})
    public ResponseEntity<byte[]> document(@PathVariable(required = false) String number, @PathVariable Long documentId) throws UnsupportedEncodingException {
        Document document = documentRepository.findById(documentId);
        if(document == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        String fileName = URLEncoder.encode(document.getFileName(), UTF_8.toString());
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + fileName)
                .header("Content-Type", DocumentUtils.getContentType(FilenameUtils.getExtension(document.getFileName())))
                .body(document.getContent());
    }

    @GetMapping(path = {"/export/xlsx/lt"})
    public ResponseEntity<byte[]> exportLtToXlsx(LtDossierSearchForm searchForm, Locale locale) throws IOException {
        try(Workbook workbook = new XSSFWorkbook()) {

            PaginatedList<DossierListItem> searchResponse = dossierRepository.ltDossierSearch(searchForm);

            Sheet sheet = workbook.createSheet();
            sheet.setColumnWidth(0, 2000);
            sheet.setColumnWidth(1, 4000);
            sheet.setColumnWidth(2, 5000);
            sheet.setColumnWidth(3, 15000);
            sheet.setColumnWidth(4, 15000);
            sheet.setColumnWidth(5, 5000);

            Row header = sheet.createRow(0);
            Cell headerCell = header.createCell(0, CellType.NUMERIC);
            headerCell.setCellValue(messageSource.getMessage("ip.list.no", null, locale));
            headerCell = header.createCell(1, CellType.NUMERIC);
            headerCell.setCellValue(messageSource.getMessage("patent.list.applicationnumber", null, locale));
            headerCell = header.createCell(2, CellType.NUMERIC);
            headerCell.setCellValue(messageSource.getMessage("patent.list.number", null, locale));
            headerCell = header.createCell(3, CellType.STRING);
            headerCell.setCellValue(messageSource.getMessage("patent.list.title", null, locale));
            headerCell = header.createCell(4, CellType.STRING);
            headerCell.setCellValue(messageSource.getMessage("patent.list.applicant", null, locale));
            headerCell = header.createCell(5, CellType.STRING);
            headerCell.setCellValue(messageSource.getMessage("patent.list.status", null, locale));

            Row row;
            int i = 1;
            for (DossierListItem item : searchResponse.getData()) {
                row = sheet.createRow(i);

                String title;
                String status;
                if (locale.equals(Locale.ENGLISH)) {
                    title = item.getTitleEn();
                    status = item.getStatusEn();
                }else {
                    title = item.getTitleLt();
                    status = item.getStatusLt();
                }

                Cell cell = row.createCell(0);
                cell.setCellValue((double)searchForm.getOffset() + i);
                cell = row.createCell(1);
                cell.setCellValue(item.getApplicationNumber());
                cell = row.createCell(2);
                cell.setCellValue(item.getNumber());
                cell = row.createCell(3);
                cell.setCellValue(title);

                cell = row.createCell(4);
                StringBuilder applicantNames = new StringBuilder();
                for (Person person : item.getApplicants()){
                    applicantNames.append(person.getFreeformName()).append(", ");
                }
                cell.setCellValue(applicantNames.toString());

                cell = row.createCell(5);
                cell.setCellValue(status);

                i++;
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            return ResponseEntity.ok()
                    .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .header("Content-Disposition", "attachment; filename=export.xlsx")
                    .body(outputStream.toByteArray());
        }
    }

    @GetMapping(path = {"/export/xlsx/eu"})
    public ResponseEntity<byte[]> exportEuToXlsx(EuDossierSearchForm searchForm, Locale locale) throws IOException {
        try(Workbook workbook = new XSSFWorkbook()) {

            PaginatedList<DossierListItem> searchResponse = dossierRepository.euDossierSearch(searchForm);

            Sheet sheet = workbook.createSheet();
            sheet.setColumnWidth(0, 2000);
            sheet.setColumnWidth(1, 7000);
            sheet.setColumnWidth(2, 6000);
            sheet.setColumnWidth(3, 15000);
            sheet.setColumnWidth(4, 15000);
            sheet.setColumnWidth(5, 5000);

            Row header = sheet.createRow(0);
            Cell headerCell = header.createCell(0, CellType.NUMERIC);
            headerCell.setCellValue(messageSource.getMessage("patent.eu.list.no", null, locale));
            headerCell = header.createCell(1, CellType.NUMERIC);
            headerCell.setCellValue(messageSource.getMessage("patent.eu.list.applicationnumber", null, locale));
            headerCell = header.createCell(2, CellType.NUMERIC);
            headerCell.setCellValue(messageSource.getMessage("patent.eu.list.number", null, locale));
            headerCell = header.createCell(3, CellType.STRING);
            headerCell.setCellValue(messageSource.getMessage("patent.eu.list.title", null, locale));
            headerCell = header.createCell(4, CellType.STRING);
            headerCell.setCellValue(messageSource.getMessage("patent.eu.list.applicant", null, locale));
            headerCell = header.createCell(5, CellType.STRING);
            headerCell.setCellValue(messageSource.getMessage("patent.eu.list.status", null, locale));

            Row row;
            int i = 1;
            for (DossierListItem item : searchResponse.getData()) {
                row = sheet.createRow(i);

                String title;
                String status;
                if (locale.equals(Locale.ENGLISH)) {
                    title = item.getTitleEn();
                    status = item.getStatusEn();
                }else {
                    title = item.getTitleLt();
                    status = item.getStatusLt();
                }

                Cell cell = row.createCell(0);
                cell.setCellValue((double)searchForm.getOffset() + i);
                cell = row.createCell(1);
                cell.setCellValue(item.getApplicationNumber());
                cell = row.createCell(2);
                cell.setCellValue(item.getNumber());
                cell = row.createCell(3);
                cell.setCellValue(title);

                cell = row.createCell(4);
                StringBuilder ownerNames = new StringBuilder();
                for (Person person : item.getOwners()){
                    ownerNames.append(person.getFreeformName()).append(", ");
                }
                cell.setCellValue(ownerNames.toString());

                cell = row.createCell(5);
                cell.setCellValue(status);

                i++;
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            return ResponseEntity.ok()
                    .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .header("Content-Disposition", "attachment; filename=export.xlsx")
                    .body(outputStream.toByteArray());
        }
    }
}
