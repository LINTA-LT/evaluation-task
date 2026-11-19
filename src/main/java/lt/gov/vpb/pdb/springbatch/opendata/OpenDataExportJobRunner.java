package lt.gov.vpb.pdb.springbatch.opendata;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.util.Date;

import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;
import static java.time.temporal.ChronoUnit.MONTHS;
import static lt.gov.vpb.pdb.springbatch.opendata.ExportType.MONTHLY;
import static lt.gov.vpb.pdb.springbatch.opendata.OpenDataExportJobConfiguration.JOB_PARAM_EXPORT_TYPE;
import static lt.gov.vpb.pdb.springbatch.opendata.OpenDataExportJobConfiguration.JOB_PARAM_YEARMONTH;

@Slf4j
@EnableScheduling
@Component
public class OpenDataExportJobRunner  implements ApplicationListener<OpenDataExportCommand> {

    private boolean schedulerEnabled;

    private JobLauncher jobLauncher;
    private Job exportOpenDataJob;
    private JobExecution execution;
    private SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter YYYY_MM = new DateTimeFormatterBuilder()
            .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
            .appendLiteral('-')
            .appendValue(MONTH_OF_YEAR, 2)
            .toFormatter();

    @Autowired
    public OpenDataExportJobRunner(@Value("${opendata.scheduler.enabled:false}") boolean schedulerEnabled,
                                   JobLauncher jobLauncher,
                                   Job exportOpenDataJob) {
        log.info("opendata.scheduler.enabled={} on this node", schedulerEnabled);
        this.schedulerEnabled = schedulerEnabled;
        this.jobLauncher = jobLauncher;
        this.exportOpenDataJob = exportOpenDataJob;
    }

    @Scheduled(cron = "${opendata.scheduler.cron}")
    public void runScheduled() {
        if(!schedulerEnabled){
            log.warn("Scheduler is not enabled on this node.");
            return;
        }
        run();
    }

    @Override
    public void onApplicationEvent(OpenDataExportCommand event) {
        run(event);
    }

    public void run() {
        run(null);
    }

    public void run(OpenDataExportCommand command) {
        JobParametersBuilder paramBuilder = new JobParametersBuilder();
        paramBuilder.addString("launchDate", dt.format(new Date()));
        String defaultYearMonth = LocalDate.now().minus(1, MONTHS).format(YYYY_MM);
        if (command == null) {
            //set defaults
            paramBuilder.addString(JOB_PARAM_EXPORT_TYPE, MONTHLY.toString());
            paramBuilder.addString(JOB_PARAM_YEARMONTH, defaultYearMonth);
        } else {
            paramBuilder.addString(JOB_PARAM_EXPORT_TYPE, command.getExportType().toString());
            if (command.getExportType() == MONTHLY
                    && command.getYear() != null && command.getMonth() != null) {
                String month = command.getMonth() < 10 ? "0" + command.getMonth() : command.getMonth().toString();
                paramBuilder.addString(JOB_PARAM_YEARMONTH, command.getYear() + "-" + month);
            } else {
                paramBuilder.addString(JOB_PARAM_YEARMONTH, defaultYearMonth);
            }
        }

        try {
            if (execution == null || !execution.isRunning()) {
                execution = jobLauncher.run(exportOpenDataJob, paramBuilder.toJobParameters());
            }
            log.info("BatchJob Status : {}", execution.getStatus());
            log.info("Exit Status : {}", execution.getExitStatus().getExitCode());
        } catch (JobExecutionAlreadyRunningException | JobRestartException
                | JobInstanceAlreadyCompleteException
                | JobParametersInvalidException e) {

            log.error(e.getMessage(), e);
        }
    }

}
