package lt.gov.vpb.pdb.controller;

import lombok.extern.slf4j.Slf4j;
import lt.gov.vpb.pdb.form.DisputeSearchForm;
import lt.gov.vpb.pdb.form.DisputeSearchForm.DisputeType;
import lt.gov.vpb.pdb.model.Dispute;
import lt.gov.vpb.pdb.model.DisputeListItem;
import lt.gov.vpb.pdb.model.PaginatedList;
import lt.gov.vpb.pdb.repository.SearchService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Controller
@Slf4j
@RequestMapping("/dispute")
public class DisputeController extends BasicSearchController {

    @Autowired
    @Qualifier("disputeSearchProvider")
    SearchService<DisputeSearchForm, DisputeListItem, Dispute> searchService;

    @Autowired
    MessageSource messageSource;

    @GetMapping(path = {"/{disputeType}/", "/{disputeType}", "/{disputeType}/search-form"})
    public String searchForm(@PathVariable DisputeType disputeType, Model model, @ModelAttribute("searchForm") DisputeSearchForm searchForm) {
        searchForm.setType(disputeType);
        return disputeType + "-search-form";
    }

    @GetMapping(path = "/{disputeType}/search")
    public String appealSearch(@PathVariable DisputeType disputeType, Model model, @ModelAttribute("searchForm") DisputeSearchForm searchForm) {
        searchForm.setType(disputeType);
        PaginatedList<DisputeListItem> searchResponse = searchService.search(searchForm);
        model.addAttribute("totalHits", searchResponse.getTotalItems());
        model.addAttribute("disputeItems", searchResponse.getData());
        return disputeType.in(DisputeType.cancellation, DisputeType.invalidation) ?
                "dispute-common-search-results" :
                disputeType + "-search-results";
    }

    @GetMapping(path = "/details/{id}")
    public String details(@PathVariable String id, Model model) {
        model.addAttribute("dispute", searchService.details(id));
        return "dispute-details";
    }

    @GetMapping(path = {"/{disputeType}/export/xlsx"})
    public ResponseEntity<byte[]> exportToXlsx(@PathVariable DisputeType disputeType, DisputeSearchForm searchForm, Locale locale) throws IOException {
        try(Workbook workbook = new XSSFWorkbook()) {

            if (searchForm.getType() == null) {
                searchForm.setType(disputeType);
            }
            PaginatedList<DisputeListItem> searchResponse = searchService.search(searchForm);

            Sheet sheet = workbook.createSheet();
            sheet.setColumnWidth(0, 2000);
            sheet.setColumnWidth(1, 4000);
            sheet.setColumnWidth(2, 4000);
            sheet.setColumnWidth(3, 4000);
            sheet.setColumnWidth(4, 4000);
            sheet.setColumnWidth(5, 8000);
            sheet.setColumnWidth(6, 4000);
            sheet.setColumnWidth(7, 4000);
            sheet.setColumnWidth(8, 4000);
            sheet.setColumnWidth(9, 4000);
            sheet.setColumnWidth(10, 4000);

            Row header = sheet.createRow(0);
            Cell headerCell = header.createCell(0, CellType.NUMERIC);
            headerCell.setCellValue(messageSource.getMessage("dispute.common.list.no", null, locale));
            headerCell = header.createCell(1, CellType.NUMERIC);
            String messageKey = disputeType.in(DisputeType.appeal, DisputeType.opposition) ? "dispute." + disputeType + ".list.refappnumber" : "dispute.common.list.refappnumber";
            headerCell.setCellValue(messageSource.getMessage(messageKey, null, locale));
            headerCell = header.createCell(2, CellType.STRING);
            headerCell.setCellValue(messageSource.getMessage("dispute.common.list.applicationnumber", null, locale));
            headerCell = header.createCell(3, CellType.STRING);
            headerCell.setCellValue(messageSource.getMessage("dispute.common.list.decisiondate", null, locale));
            headerCell = header.createCell(4, CellType.STRING);
            headerCell.setCellValue(messageSource.getMessage("dispute.common.list.decisionnumber", null, locale));
            headerCell = header.createCell(5, CellType.STRING);
            headerCell.setCellValue(messageSource.getMessage("dispute.common.list.disputedtrademark", null, locale));
            headerCell = header.createCell(6, CellType.STRING);
            headerCell.setCellValue(messageSource.getMessage("dispute.common.list.registrationnumber", null, locale));
            headerCell = header.createCell(7, CellType.STRING);
            headerCell.setCellValue(messageSource.getMessage("dispute.common.list.niceclassification", null, locale));
            headerCell = header.createCell(8, CellType.STRING);
            headerCell.setCellValue(messageSource.getMessage("dispute.common.list.grounds", null, locale));
            headerCell = header.createCell(9, CellType.STRING);
            headerCell.setCellValue(messageSource.getMessage("dispute.common.list.decision", null, locale));
            headerCell = header.createCell(10, CellType.STRING);
            messageKey = disputeType.in(DisputeType.appeal, DisputeType.opposition) ? "dispute." + disputeType + ".list.status" : "dispute.common.list.status";
            headerCell.setCellValue(messageSource.getMessage(messageKey, null, locale));

            Row row;
            int i = 1;
            for (DisputeListItem item : searchResponse.getData()) {
                row = sheet.createRow(i);
                Cell cell = row.createCell(0);
                cell.setCellValue((double)searchForm.getOffset() + i);
                cell = row.createCell(1);
                cell.setCellValue(item.getRefApplicationNumber());
                cell = row.createCell(2);
                cell.setCellValue(item.getApplicationNumber());
                cell = row.createCell(3);
                cell.setCellValue(item.getDecisionDate());
                cell = row.createCell(4);
                cell.setCellValue(item.getCaseNumber());
                cell = row.createCell(5);
                cell.setCellValue(item.getContestedTrademark());
                cell = row.createCell(6);
                cell.setCellValue(item.getRegistrationNumber());
                cell = row.createCell(7);
                cell.setCellValue(item.getNiceClassification());
                cell = row.createCell(8);
                cell.setCellValue(item.getGrounds());
                cell = row.createCell(9);
                if (!isEmpty(item.getDecision())) {
                    messageKey = (disputeType.in(DisputeType.appeal, DisputeType.opposition) ? "dispute." + disputeType + ".decision." : "dispute.common.decision.")
                            + item.getDecision();
                    cell.setCellValue(messageSource.getMessage(messageKey, null, locale));
                }
                cell = row.createCell(10);
                if (!isEmpty(item.getStatus())) {
                    cell.setCellValue(messageSource.getMessage("dispute.status." + item.getStatus(), null, locale));
                }
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
