package lt.gov.vpb.pdb.controller;

import lombok.extern.slf4j.Slf4j;
import lt.gov.vpb.pdb.controller.util.AddDimensionedImage;
import lt.gov.vpb.pdb.form.DsAdvancedSearchForm;
import lt.gov.vpb.pdb.form.DsSearchForm;
import lt.gov.vpb.pdb.model.Design;
import lt.gov.vpb.pdb.model.DesignListItem;
import lt.gov.vpb.pdb.model.PaginatedList;
import lt.gov.vpb.pdb.repository.SearchService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

@Controller
@RequestMapping("/design")
@Slf4j
public class DesignController extends BasicSearchController {

    @Autowired
    @Qualifier("dsSearchService")
    SearchService<DsSearchForm, DesignListItem, Design> searchService;

    @Autowired
    RestTemplate restTemplate;

    @Value("${tmdsview.image-ws-endpoint.ds}")
    String fileUriTemplate;

    @Autowired
    MessageSource messageSource;

    @GetMapping(path = {"/", "", "/search-form"})
    public String searchForm(Model model, DsSearchForm searchForm) {
        model.addAttribute("searchForm", searchForm);
        return "ds-search-form";
    }

    @GetMapping(path = "/advanced-search-form")
    public String searchFormAdvanced(Model model, DsAdvancedSearchForm searchForm) {
        model.addAttribute("searchForm", searchForm);
        return "ds-advanced-search-form";
    }

    @GetMapping(path = "/{resource}/{st13}")
    @ResponseBody
    public ResponseEntity<byte[]> image(@PathVariable String resource, @PathVariable String st13) {
        try {
            return restTemplate.getForEntity(new UriTemplate(fileUriTemplate).expand(resource, st13), byte[].class);
        } catch (RestClientException e) {
            log.warn("RestClientException while retrieving {} {}; {}", st13, resource, e.getMessage());
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/search")
    public String search(Model model, DsSearchForm searchForm) {
        PaginatedList<DesignListItem> searchResponse = searchService.search(searchForm);
        model.addAttribute("searchForm", searchForm);
        model.addAttribute("totalHits", searchResponse.getTotalItems());
        model.addAttribute("designs", searchResponse.getData());
        return "ds-search-results";
    }

    @GetMapping(path = "/advanced-search")
    public String advancedSearch(Model model, DsAdvancedSearchForm searchForm) {
        search(model, searchForm);
        return "ds-search-results";
    }

    @GetMapping(path = "/details/{id}")
    public String details(@PathVariable String id, Model model) {
        model.addAttribute("design", searchService.details(id));
        return "ds-details";
    }

    @GetMapping(path = {"/export/xlsx"})
    public ResponseEntity<byte[]> exportToXlsx(DsAdvancedSearchForm searchForm, Locale locale) throws IOException {

        try(Workbook workbook = new XSSFWorkbook()) {

            PaginatedList<DesignListItem> searchResponse = searchService.search(searchForm);

            Sheet sheet = workbook.createSheet();
            sheet.setColumnWidth(0, 2000);
            sheet.setColumnWidth(1, 8000);
            sheet.setColumnWidth(2, 4000);
            sheet.setColumnWidth(3, 4000);
            sheet.setColumnWidth(4, 4000);
            sheet.setColumnWidth(5, 4000);
            sheet.setColumnWidth(6, 4000);
            sheet.setColumnWidth(7, 4000);
            sheet.setColumnWidth(8, 14000);

            Row header = sheet.createRow(0);
            Cell headerCell = header.createCell(0, CellType.NUMERIC);
            headerCell.setCellValue(messageSource.getMessage("ip.list.no", null, locale));
            headerCell = header.createCell(1, CellType.NUMERIC);
            headerCell.setCellValue(messageSource.getMessage("ip.list.ds.designtitle", null, locale));
            headerCell = header.createCell(2, CellType.STRING);
            headerCell.setCellValue(messageSource.getMessage("ip.list.applicationnumber", null, locale));
            headerCell = header.createCell(3, CellType.STRING);
            headerCell.setCellValue(messageSource.getMessage("ip.list.registrationnumber", null, locale));
            headerCell = header.createCell(4, CellType.STRING);
            headerCell.setCellValue(messageSource.getMessage("ip.list.ds.locarno", null, locale));
            headerCell = header.createCell(5, CellType.STRING);
            headerCell.setCellValue(messageSource.getMessage("ip.list.ds.graphics", null, locale));
            headerCell = header.createCell(6, CellType.STRING);
            headerCell.setCellValue(messageSource.getMessage("ip.list.ds.noofsamples", null, locale));
            headerCell = header.createCell(7, CellType.STRING);
            headerCell.setCellValue(messageSource.getMessage("ip.list.expirydate", null, locale));
            headerCell = header.createCell(8, CellType.STRING);
            headerCell.setCellValue(messageSource.getMessage("ip.list.status", null, locale));

            Drawing drawing = sheet.createDrawingPatriarch();

            Row row;
            int i = 1;
            ResponseEntity<byte[]> imageEntity;
            for (DesignListItem item : searchResponse.getData()) {
                row = sheet.createRow(i);
                imageEntity = image("thumbnail", item.getSt13());
                Cell cell = row.createCell(0);
                cell.setCellValue((double)searchForm.getOffset() + i);
                cell = row.createCell(1);
                cell.setCellValue(item.getDescription());
                cell = row.createCell(2);
                cell.setCellValue(item.getApplicationNumber());
                cell = row.createCell(3);
                cell.setCellValue(item.getRegistrationNumber());
                cell = row.createCell(4);
                cell.setCellValue(item.getLocarnoClass());
                cell = row.createCell(6);
                cell.setCellValue(item.getNoOfSamples());
                cell = row.createCell(7);
                cell.setCellValue(item.getExpires());
                cell = row.createCell(8);
                cell.setCellValue(item.getStatusLt());

                if (imageEntity != null && imageEntity.getStatusCode() == HttpStatus.OK) {
                    ByteArrayInputStream bais = new ByteArrayInputStream(imageEntity.getBody());
                    BufferedImage bufferedImage = ImageIO.read(bais);
                    int height = bufferedImage.getHeight();
                    int width = bufferedImage.getWidth();
                    int relativeHeight = (int) (((double) height / width) * 28.5);
                    new AddDimensionedImage().addImageToSheet(5,
                            sheet.getPhysicalNumberOfRows() - 1,
                            sheet,
                            drawing,
                            imageEntity.getBody(),
                            30,
                            relativeHeight,
                            AddDimensionedImage.EXPAND_ROW);
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
