package lt.gov.vpb.pdb.controller;

import lombok.extern.slf4j.Slf4j;
import lt.gov.vpb.pdb.controller.util.AddDimensionedImage;
import lt.gov.vpb.pdb.controller.util.ConstantUtils;
import lt.gov.vpb.pdb.form.TmAdvancedSearchForm;
import lt.gov.vpb.pdb.form.TmSearchForm;
import lt.gov.vpb.pdb.model.PaginatedList;
import lt.gov.vpb.pdb.model.Trademark;
import lt.gov.vpb.pdb.model.TrademarkListItem;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import static java.io.File.separatorChar;
import static java.nio.file.Files.readAllBytes;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Controller
@RequestMapping("/trademark")
@Slf4j
public class TrademarkController extends BasicSearchController {

    @Autowired
    @Qualifier("tmSearchProvider")
    SearchService<TmSearchForm, TrademarkListItem, Trademark> searchService;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    MessageSource messageSource;

    @Value("${tmdsview.binarydata-dir.tm}")
    String binaryDataDir;

    @Value("${tmdsview.image-ws-endpoint.tm}")
    String imageUriTemplate;

    @GetMapping(path = {"/", "", "/search-form"})
    public String searchForm(Model model, TmSearchForm searchForm, Locale locale) {
        model.addAttribute("searchForm", searchForm);
        model.addAttribute("locale", locale.getLanguage());
        return "tm-search-form";
    }

    @GetMapping(path = "/advanced-search-form")
    public String searchFormAdvanced(Model model, TmAdvancedSearchForm searchForm, Locale locale) {
        model.addAttribute("searchForm", searchForm);
        model.addAttribute("locale", locale.getLanguage());
        return "tm-advanced-search-form";
    }

    @GetMapping(path = "/{resource}/{id}")
    public ResponseEntity<byte[]> image(@PathVariable String resource, @PathVariable String id) throws IOException {
        Trademark trademark = searchService.details(id);

        if("binary".equals(resource)) {
            if (!StringUtils.isEmpty(trademark.getFilename())) {
                return new ResponseEntity<>(readAllBytes(new File(binaryDataDir + separatorChar + trademark.getFilename()).toPath()), HttpStatus.OK);
            }
            log.warn("Requested binary for {} cannot be identified", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            try {
                return restTemplate.getForEntity(new UriTemplate(imageUriTemplate).expand(resource, trademark.getApplicationNumber()), byte[].class);
            } catch (RestClientException e) {
                log.warn("RestClientException while retrieving {} {}; {}", trademark.getApplicationNumber(), resource, e.getMessage());
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
        }
    }

    @GetMapping(path = "/search")
    public String search(Model model, TmSearchForm searchForm) {
        PaginatedList<TrademarkListItem> tmSearchResponse = searchService.search(searchForm);
        tmSearchResponse.getData().stream().filter(tm -> tm.getApplicationNumber().equals("2000 0069"))
                .findAny().ifPresent(tm -> tmSearchResponse.setTotalItems(tmSearchResponse.getTotalItems() -1));
        tmSearchResponse.getData().removeIf(trademark -> trademark.getApplicationNumber().equals("2000 0069"));
        model.addAttribute("searchForm", searchForm);
        model.addAttribute("totalHits", tmSearchResponse.getTotalItems());
        model.addAttribute("trademarks", tmSearchResponse.getData());
        return "tm-search-results";
    }

    @GetMapping(path = "/advanced-search")
    public String advancedSearch(Model model, TmAdvancedSearchForm searchForm) {
        search(model, searchForm);
        return "tm-search-results";
    }

    @GetMapping(path = "/details/{id}")
    public String details(@PathVariable String id, Model model) {
        model.addAttribute("trademark", searchService.details(id));
        return "tm-details";
    }

    @GetMapping(path = {"/export/xlsx"})
    public ResponseEntity<byte[]> exportToXlsx(TmAdvancedSearchForm searchForm, Locale locale) throws IOException {
        try(Workbook workbook = new XSSFWorkbook()) {

            PaginatedList<TrademarkListItem> searchResponse = searchService.search(searchForm);

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
            headerCell.setCellValue(messageSource.getMessage("ip.list.tm.verbalelement", null, locale));
            headerCell = header.createCell(2, CellType.STRING);
            headerCell.setCellValue(messageSource.getMessage("ip.list.tm.type", null, locale));
            headerCell = header.createCell(3, CellType.STRING);
            headerCell.setCellValue(messageSource.getMessage("ip.list.applicationnumber", null, locale));
            headerCell = header.createCell(4, CellType.STRING);
            headerCell.setCellValue(messageSource.getMessage("ip.list.registrationnumber", null, locale));
            headerCell = header.createCell(5, CellType.STRING);
            headerCell.setCellValue(messageSource.getMessage("ip.list.tm.graphics", null, locale));
            headerCell = header.createCell(6, CellType.STRING);
            headerCell.setCellValue(messageSource.getMessage("ip.list.tm.niceclass", null, locale));
            headerCell = header.createCell(7, CellType.STRING);
            headerCell.setCellValue(messageSource.getMessage("ip.list.expirydate", null, locale));
            headerCell = header.createCell(8, CellType.STRING);
            headerCell.setCellValue(messageSource.getMessage("ip.list.status", null, locale));

            Drawing drawing = sheet.createDrawingPatriarch();

            Row row;
            int i = 1;
            ResponseEntity<byte[]> imageEntity;
            for (TrademarkListItem item : searchResponse.getData()) {
                row = sheet.createRow(i);
                if (!isEmpty(item.getFilename())) {
                    row.setHeight((short) 1500);
                    String type = "thumbnail";
                    imageEntity = image(type, item.getApplicationNumber());
                } else {
                    imageEntity = null;
                }
                Cell cell = row.createCell(0);
                cell.setCellValue((double)searchForm.getOffset() + i);
                cell = row.createCell(1);
                cell.setCellValue(item.getTrademark());
                cell = row.createCell(2);
                cell.setCellValue(item.getTyfeature());
                cell = row.createCell(3);
                cell.setCellValue(item.getApplicationNumber());
                cell = row.createCell(4);
                cell.setCellValue(item.getRegistrationNumber());
                cell = row.createCell(6);
                cell.setCellValue(item.getClassification());
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
