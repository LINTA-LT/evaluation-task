package lt.gov.vpb.pdb.springbatch.opendata.file;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lt.gov.vpb.pdb.springbatch.opendata.ExportType;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.String.format;
import static lt.gov.vpb.pdb.springbatch.opendata.ExportType.FULL;
import static lt.gov.vpb.pdb.springbatch.opendata.OpenDataExportJobConfiguration.*;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * Holds all the information for the generation of a report of the syncing
 * process of central index update
 */
@Slf4j
@Data
public class ExportEmailReport implements Serializable {

    private static final long serialVersionUID = 7352079121106781847L;

    private ExportType exportType;

    private String yearMonth;

    private final DateFormat dateTimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @Value("${opendata.page-url}")
    private URL openDataUrl;

    @Value("${opendata.logs-dir}")
    private String loggingDirectory;

    private int tmReadCount = -1;
    private int tmSkipCount = -1;
    private int tmProcessSkipCount = -1;
    private int tmReadSkipCount = -1;
    private int dsReadCount = -1;
    private int dsSkipCount = -1;
    private int dsProcessSkipCount = -1;
    private int dsReadSkipCount = -1;

    public ExportEmailReport(JobExecution jobExecution) {
        this.yearMonth = jobExecution.getJobParameters().getString(JOB_PARAM_YEARMONTH);
        String exportTypeJobParam = jobExecution.getJobParameters().getString(JOB_PARAM_EXPORT_TYPE);
        this.exportType = isNotEmpty(exportTypeJobParam) ? ExportType.valueOf(exportTypeJobParam) : ExportType.MONTHLY;

        for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
            if (stepExecution.getStepName().equals(STEP_EXPORT_TRADEMARKS)) {
                tmReadCount = stepExecution.getExecutionContext().getInt(STEP_PARAM_READ_COUNT);
                tmSkipCount = stepExecution.getExecutionContext().getInt(STEP_PARAM_SKIP_COUNT);
                tmProcessSkipCount = stepExecution.getExecutionContext().getInt(STEP_PARAM_PROCESS_SKIP_COUNT);
                tmReadSkipCount = stepExecution.getExecutionContext().getInt(STEP_PARAM_READ_SKIP_COUNT);
            } else {
                dsReadCount = stepExecution.getExecutionContext().getInt(STEP_PARAM_READ_COUNT);
                dsSkipCount = stepExecution.getExecutionContext().getInt(STEP_PARAM_SKIP_COUNT);
                dsProcessSkipCount = stepExecution.getExecutionContext().getInt(STEP_PARAM_PROCESS_SKIP_COUNT);
                dsReadSkipCount = stepExecution.getExecutionContext().getInt(STEP_PARAM_READ_SKIP_COUNT);

            }
        }
    }

    public String getSubject() {
        // Set Subject: header field
        return format("VPB atvirų duomenų %s %s ataskaita",
                yearMonth,
                exportType == ExportType.MONTHLY ? "mėnesinė" : "pilna");
    }

    public String getBody() {
        StringBuffer sb = new StringBuffer();
        sb.append("Duomenys pasiekiami šiuo adresu " + openDataUrl);
        sb.append("\r\n\r\n");
        //sb.append(":: Duomenų generavimo procesas truko nuo " + dateTimeFormat.format(beginDate) + " iki "+ dateTimeFormat.format(endDate));
        sb.append("\r\n\t" + "Prekės ženklų skaičius: " + tmReadCount);
        sb.append("\r\n\t" + "Dizainų skaičius: " + dsReadCount);
        return sb.toString();
    }

    public void toFile() {
        try (Writer writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(getPath(generateFileName())), "utf-8"))) {
            writer.write(getBody());
        } catch (IOException ex) {
            // report
            log.error("Exception while writing report ", ex);
        }
    }


    private String generateFileName() {
        Assert.notNull(exportType);
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HHmmss");
        String fileName = dateFormat.format(new Date()) + "-";
        fileName += exportType.toString() + "-report.log";
        if (exportType == FULL) {
            fileName += "FULL";
        } else {
            fileName += "DIFF";
        }
        fileName += "-report.log";

        log.info(fileName);
        return fileName;
    }

    private String getPath(String fileName) throws IOException {
        verifyCreateTargetDirectory();
        return loggingDirectory != null ? loggingDirectory + File.separator + fileName : fileName;
    }

    private void verifyCreateTargetDirectory() {
        File f = new File(loggingDirectory);
        if (!f.exists())
            f.mkdirs();
    }

}
