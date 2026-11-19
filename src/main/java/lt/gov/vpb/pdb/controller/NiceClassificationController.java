package lt.gov.vpb.pdb.controller;

import lombok.extern.slf4j.Slf4j;
import lt.gov.vpb.pdb.form.NiceClassificationSearchForm;
import lt.gov.vpb.pdb.model.Details;
import lt.gov.vpb.pdb.model.NiceClassificationListItem;
import lt.gov.vpb.pdb.model.PaginatedList;
import lt.gov.vpb.pdb.repository.SearchService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

@Controller
@Slf4j
@RequestMapping("/niceclass")
public class NiceClassificationController extends BasicSearchController {

    //TODO check in database for latest available year
    @Value("${niceclass.currentversion-year}")
    Integer currentVersionYear;

    @Autowired
    @Qualifier("niceClassificationSearchProvider")
    SearchService<NiceClassificationSearchForm, NiceClassificationListItem, Details> searchService;

    @Autowired
    MessageSource messageSource;

    @ModelAttribute
    @Override
    public void addAttributes(Model model) {
        super.addAttributes(model);
        model.addAttribute("currentVersionYear", currentVersionYear);
    }

    @GetMapping(path = {"", "/", "/search-form"})
    public String searchForm(@ModelAttribute("searchForm") NiceClassificationSearchForm searchForm) {
        return "nice-classification-search-form";
    }

    @GetMapping(path = "/search")
    public String search(Model model, @ModelAttribute("searchForm") NiceClassificationSearchForm searchForm) {
        PaginatedList<NiceClassificationListItem> searchResponse = searchService.search(searchForm);
        model.addAttribute("totalHits", searchResponse.getTotalItems());
        model.addAttribute("niceClassItems", searchResponse.getData());
        return "nice-classification-search-results";
    }

    @GetMapping(path = {"/export/xlsx"})
    public ResponseEntity<byte[]> exportToXlsx(NiceClassificationSearchForm searchForm, Locale locale) throws IOException {
        try(Workbook workbook = new XSSFWorkbook()){
            PaginatedList<NiceClassificationListItem> searchResponse = searchService.search(searchForm);
            Sheet sheet = workbook.createSheet();
            sheet.setColumnWidth(0, 2000);
            sheet.setColumnWidth(1, 2000);
            sheet.setColumnWidth(2, 14000);
            sheet.setColumnWidth(3, 6000);
            sheet.setColumnWidth(4, 14000);

            Row header = sheet.createRow(0);

            CellStyle headerStyle = workbook.createCellStyle();

            XSSFFont font = ((XSSFWorkbook) workbook).createFont();
            font.setFontName("Arial");
            font.setFontHeightInPoints((short) 16);
            font.setBold(true);
            headerStyle.setFont(font);

            Cell headerCell = header.createCell(0, CellType.NUMERIC);
            headerCell.setCellValue(messageSource.getMessage("niceclass.list.no", null, locale));
            headerCell.setCellStyle(headerStyle);
            headerCell = header.createCell(1, CellType.NUMERIC);
            headerCell.setCellValue(messageSource.getMessage("niceclass.list.classnumber", null, locale));
            headerCell.setCellStyle(headerStyle);
            headerCell = header.createCell(2, CellType.STRING);
            headerCell.setCellValue(messageSource.getMessage("niceclass.list.classtermlt", null, locale));
            headerCell.setCellStyle(headerStyle);
            headerCell = header.createCell(3, CellType.STRING);
            headerCell.setCellValue(messageSource.getMessage("niceclass.list.basicnumber", null, locale));
            headerCell.setCellStyle(headerStyle);
            headerCell = header.createCell(4, CellType.STRING);
            headerCell.setCellValue(messageSource.getMessage("niceclass.list.classtermen", null, locale));
            headerCell.setCellStyle(headerStyle);

            Row row;
            int i=1;
            for(NiceClassificationListItem item : searchResponse.getData()){
                row = sheet.createRow(i);
                Cell cell = row.createCell(0);
                cell.setCellValue(i);
                cell = row.createCell(1);
                cell.setCellValue(item.getClassNumber());
                cell = row.createCell(2);
                cell.setCellValue(item.getClassTermLt());
                cell = row.createCell(3);
                cell.setCellValue(item.getBasicNumber());
                cell = row.createCell(4);
                cell.setCellValue(item.getClassTermEn());
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
