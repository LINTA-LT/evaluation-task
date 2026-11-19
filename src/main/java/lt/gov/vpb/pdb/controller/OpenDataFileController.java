package lt.gov.vpb.pdb.controller;

import lombok.extern.slf4j.Slf4j;
import lt.gov.vpb.pdb.model.opendata.MonthlyData;
import lt.gov.vpb.pdb.springbatch.opendata.OpenDataExportCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static java.nio.file.Files.readAllBytes;

@Controller
@Slf4j
@RequestMapping("/opendata")
public class OpenDataFileController extends BasicSearchController {

    @Value("${opendata.files-dir}")
    private String directory;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @ModelAttribute
    @Override
    public void addAttributes(Model model) {
        super.addAttributes(model);
        model.addAttribute("lastUpdated", referenceDataRepository.getLastAuditLog("patents")
                .map(LocalDate::from)
                .orElse(LocalDate.now()));
    }

    @GetMapping(path = {"/trademark", "/trademark/list"})
    public String listTrademarkFiles(Model model) {
        log.info("opendata.files-dir={}", directory);
        prepareOpenDataModel(model);
        return "opendata-tm-files-list";
    }

    @GetMapping(path = {"/design", "/design/list"})
    public String listDesignFiles(Model model) {
        prepareOpenDataModel(model);
        return "opendata-ds-files-list";
    }

    @GetMapping(value = "/export/monthly")
    public ResponseEntity syncIncremental(@RequestParam Integer year, @RequestParam Integer month) {
        eventPublisher.publishEvent(OpenDataExportCommand.monthly(this, year, month));
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(value = "/export/full")
    public ResponseEntity syncFull() {
        eventPublisher.publishEvent(OpenDataExportCommand.full(this));
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(value = "/file/{filename}")
    public ResponseEntity<byte[]> getFile(@PathVariable String filename) {

        try {
            if (filename.endsWith(".zip")) {
                return ResponseEntity.ok()
                        .header("Content-Type", "application/zip")
                        .header("Content-Disposition", "attachment; filename=" + filename)
                        .body(readAllBytes(Paths.get(directory, filename)));
            }
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    private void prepareOpenDataModel(Model model) {
        Map<String, MonthlyData> mdataMap = new TreeMap<>(Comparator.reverseOrder());
        MonthlyData fullExportData = null;
        try {
            for (Path path : Files.list(Paths.get(directory)).collect(Collectors.toList())) {
                File file = path.toFile();
                if (file.isFile() && file.getName().endsWith(".zip")) {
                    String yearMonth = file.getName().substring(3, 10);
                    if (file.getName().contains("FULL")) {
                        if (fullExportData == null) {
                            fullExportData = new MonthlyData();
                        }
                        fullExportData.setYearMonth(yearMonth);
                        if (file.getName().startsWith("TM-")) {
                            fullExportData.getTrademarkFiles().add(path.getFileName());
                        } else if (file.getName().startsWith("DS-")) {
                            fullExportData.getDesignFiles().add(path.getFileName());
                        }
                    } else {
                        MonthlyData mdata;
                        if (mdataMap.get(yearMonth) == null) {
                            mdata = new MonthlyData();
                            mdata.setYearMonth(yearMonth);
                            mdataMap.put(yearMonth, mdata);
                        } else {
                            mdata = mdataMap.get(yearMonth);
                        }
                        if (file.getName().startsWith("TM-")) {
                            mdata.getTrademarkFiles().add(path.getFileName());
                        } else if (file.getName().startsWith("DS-")) {
                            mdata.getDesignFiles().add(path.getFileName());
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        model.addAttribute("monthlyData", mdataMap.values());
        model.addAttribute("fullData", fullExportData);
    }
}
