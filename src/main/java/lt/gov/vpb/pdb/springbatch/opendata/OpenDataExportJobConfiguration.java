package lt.gov.vpb.pdb.springbatch.opendata;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lt.gov.vpb.pdb.springbatch.EmailSenderService;
import lt.gov.vpb.pdb.springbatch.opendata.ds.DesignItemProcessor;
import lt.gov.vpb.pdb.springbatch.opendata.file.Archiver;
import lt.gov.vpb.pdb.springbatch.opendata.file.ExportEmailReport;
import lt.gov.vpb.pdb.springbatch.opendata.file.FileDetails;
import lt.gov.vpb.pdb.springbatch.opendata.file.upload.PackageUploader;
import lt.gov.vpb.pdb.springbatch.opendata.tm.TrademarkItemProcessor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.WebApplicationContext;
import org.tmdn.cti.repository.jpa.domain.design.Design;
import org.tmdn.cti.repository.jpa.domain.trademark.TradeMark;

import javax.persistence.EntityManagerFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static lt.gov.vpb.pdb.springbatch.opendata.ExportType.FULL;
import static lt.gov.vpb.pdb.springbatch.opendata.ExportType.MONTHLY;

@Configuration
@Slf4j
@EnableBatchProcessing
public class OpenDataExportJobConfiguration implements JobExecutionListener, StepExecutionListener {

    protected static int chunkSize = 1000;
    protected static int throttleLimit = 100;//should be less than: MAX_POOL_SIZE
    protected static int maxPoolSize = 100;
    protected static int jpaPagingSize = 1000;

    public static final String STEP_EXPORT_TRADEMARKS = "exportTrademarksStep";
    public static final String STEP_EXPORT_DESIGNS = "exportDesignsStep";

    public static final String JOB_PARAM_YEARMONTH = "yearMonth";
    public static final String JOB_PARAM_EXPORT_TYPE = "exportType";

    public static final String STEP_PARAM_READ_COUNT = "readCount";
    public static final String STEP_PARAM_READ_SKIP_COUNT = "readSkipCount";
    public static final String STEP_PARAM_PROCESS_SKIP_COUNT = "processSkipCount";
    public static final String STEP_PARAM_SKIP_COUNT = "skipCount";

    public static final String TM_MONTHLY_QUERY = "select tm from TradeMark as tm where " +
            "(" +
            "function('date_format', tm.insertedAt, '%Y-%m') = :yearMonth " +
            "or " +
            "function('date_format', tm.updatedAt, '%Y-%m') = :yearMonth " +
            ") " +
            "and " +
            "tm.duplicate = false ";

    //all except current/ongoing month
    public static final String TM_FULL_QUERY = "select tm from TradeMark as tm where " +
            "function('date_format', tm.insertedAt, '%Y-%m') != :yearMonth " +
            "and " +
            "function('date_format', tm.updatedAt, '%Y-%m') != :yearMonth " +
            "and " +
            "tm.duplicate = false ";

    public static final String DS_MONTHLY_QUERY = "select ds from Design as ds where " +
            "( function('date_format', ds.insertedAt, '%Y-%m') = :yearMonth " +
            "or " +
            "function('date_format', ds.updatedAt, '%Y-%m') = :yearMonth ) " +
            "and isInternationalRegistration=false " +
            "and ds.duplicate = false ";

    //all except current/ongoing month
    public static final String DS_FULL_QUERY = "select ds from Design as ds where " +
            "( function('date_format', ds.insertedAt, '%Y-%m') != :yearMonth " +
            "or " +
            "function('date_format', ds.updatedAt, '%Y-%m') != :yearMonth ) " +
            "and isInternationalRegistration=false " +
            "and ds.duplicate = false ";

    @Autowired
    @Qualifier("tmviewEntityManager")
    private EntityManagerFactory tmviewEntityManager;

    @Autowired
    @Qualifier("dsviewEntityManager")
    private EntityManagerFactory dsviewEntityManager;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    @Value("${opendata.files-dir}")
    private Path targetDirectory;

    @Autowired
    private EmailSenderService emailSenderService;

    @Bean
    public Job openDataExportJob(JobBuilderFactory jobs, Step exportTrademarksStep, Step exportDesignsStep) {
        return jobs.get("openDataExportJob")
                .listener(this)
                .start(exportDesignsStep)
                .next(exportTrademarksStep)
                .build();
    }

    @SneakyThrows
    @Override
    @BeforeJob
    public void beforeJob(JobExecution jobExecution) {
        log.info("Before job: cleanup existing files if exist.");
        JobParameters jobParameters = jobExecution.getJobParameters();
        List<Path> filesList = Files.list(targetDirectory).collect(Collectors.toList());
        for (Path path : filesList) {
            if (FULL.toString().equals(jobParameters.getString(JOB_PARAM_EXPORT_TYPE))) {
                if (path.toFile().isFile()
                        && path.toFile().getName().contains(jobParameters.getString(JOB_PARAM_YEARMONTH))
                        && path.toFile().getName().contains("-FULL-")
                ) {
                    log.info("Deleting file {}", path.toFile().getName());
                    Files.delete(path);
                }
            } else {
                if (path.toFile().isFile()
                        && path.toFile().getName().contains(jobParameters.getString(JOB_PARAM_YEARMONTH))
                        && !path.toFile().getName().contains("-FULL-")
                ) {
                    log.info("Deleting file {}", path.toFile().getName());
                    Files.delete(path);
                }
            }
        }
        log.info("## Removing the files " + jobExecution.getJobParameters().getString(JOB_PARAM_EXPORT_TYPE) + " xml Extraction ##");

        log.info("## Start " + jobExecution.getJobParameters().getString(JOB_PARAM_EXPORT_TYPE) + " xml Extraction ##");
    }

    @Override
    @AfterJob
    public void afterJob(JobExecution execution) {
        //ItemStreamReader<Design> designItemStreamReader = webApplicationContext.getBean(ItemStreamReader.class);

        TrademarkItemProcessor trademarkItemProcessor = webApplicationContext.getBean(TrademarkItemProcessor.class);
        DesignItemProcessor designItemProcessor = webApplicationContext.getBean(DesignItemProcessor.class);
        log.info("TM conversion errors: {}", trademarkItemProcessor.getConversionErrors());
        log.info("DS conversion errors: {}", designItemProcessor.getConversionErrors());

        ExportEmailReport exportEmailReport = new ExportEmailReport(execution);
        beanFactory.autowireBean(exportEmailReport);
        emailSenderService.sendMail(exportEmailReport.getSubject(), exportEmailReport.getBody());
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        //nothing to do
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        stepExecution.getExecutionContext().put(STEP_PARAM_READ_COUNT, stepExecution.getReadCount());
        stepExecution.getExecutionContext().put(STEP_PARAM_SKIP_COUNT, stepExecution.getSkipCount());
        stepExecution.getExecutionContext().put(STEP_PARAM_PROCESS_SKIP_COUNT, stepExecution.getProcessSkipCount());
        stepExecution.getExecutionContext().put(STEP_PARAM_READ_SKIP_COUNT, stepExecution.getReadSkipCount());
        return stepExecution.getExitStatus();
    }

    @Bean
    @StepScope
    public ItemStreamReader<TradeMark> tmItemReader(@Value("#{jobParameters}") Map<String, Object> jobParameters) {
        JpaPagingItemReader<TradeMark> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(tmviewEntityManager);
        reader.setPageSize(jpaPagingSize);
        reader.setSaveState(false); // job not restartable due to multithreaded

        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put(JOB_PARAM_YEARMONTH, jobParameters.get(JOB_PARAM_YEARMONTH));
        reader.setParameterValues(parameterValues);

        if (jobParameters.get(JOB_PARAM_EXPORT_TYPE).equals(MONTHLY.toString())) {
            reader.setQueryString(TM_MONTHLY_QUERY);
        } else {
            reader.setQueryString(TM_FULL_QUERY);
        }
        return reader;
    }

    @Bean
    @Scope("prototype")
    public TrademarkItemProcessor trademarkItemProcessor() {
        TrademarkItemProcessor bean = new TrademarkItemProcessor();
        beanFactory.autowireBean(bean);
        return bean;
    }

    @Bean
    @Scope("prototype")
    public DesignItemProcessor designItemProcessor() {
        DesignItemProcessor bean = new DesignItemProcessor();
        beanFactory.autowireBean(bean);
        return bean;
    }

    @Bean
    @StepScope
    public ItemStreamReader<Design> designItemReader(@Value("#{jobParameters}") Map<String, Object> jobParameters) {
        JpaPagingItemReader<Design> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(dsviewEntityManager);
        reader.setPageSize(jpaPagingSize);
        reader.setSaveState(false); // job not restartable due to multithreaded
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put(JOB_PARAM_YEARMONTH, jobParameters.get(JOB_PARAM_YEARMONTH));
        reader.setParameterValues(parameterValues);

        if (jobParameters.get(JOB_PARAM_EXPORT_TYPE).equals(MONTHLY.toString())) {
            reader.setQueryString(DS_MONTHLY_QUERY);
        } else {
            //all except current/ongoing month
            reader.setQueryString(DS_FULL_QUERY);
        }
        return reader;
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(maxPoolSize);
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }

    @Bean
    @JobScope
    public Step exportTrademarksStep(StepBuilderFactory stepBuilderFactory, ItemStreamReader<TradeMark> tmItemReader,
                                     OpenDataItemWriter itemWriter, TrademarkItemProcessor trademarkItemProcessor,
                                     @Value("#{jobParameters}") Map<String, Object> jobParameters) {
        trademarkItemProcessor.setFileNameDatePrefix("");
        itemWriter.setFileNameDatePrefix("TM-"
                + jobParameters.get(JOB_PARAM_YEARMONTH).toString()
                + (jobParameters.get(JOB_PARAM_EXPORT_TYPE).equals(FULL.toString()) ? "-FULL-" : "-"));
        return stepBuilderFactory.get(STEP_EXPORT_TRADEMARKS)
                .listener(this)
                .<TradeMark, FileDetails>chunk(chunkSize)
                .reader(tmItemReader)
                .processor(trademarkItemProcessor)
                .writer(itemWriter)
                .taskExecutor(taskExecutor()).throttleLimit(throttleLimit)
                .build();
    }

    @Bean
    @JobScope
    public Step exportDesignsStep(StepBuilderFactory stepBuilderFactory, ItemStreamReader<Design> dsItemReader,
                                  OpenDataItemWriter itemWriter, DesignItemProcessor designItemProcessor,
                                  @Value("#{jobParameters}") Map<String, Object> jobParameters) {
        itemWriter.setFileNameDatePrefix("DS-"
                + jobParameters.get(JOB_PARAM_YEARMONTH).toString()
                + (jobParameters.get(JOB_PARAM_EXPORT_TYPE).equals(FULL.toString()) ? "-FULL-" : "-"));
        return stepBuilderFactory.get(STEP_EXPORT_DESIGNS)
                .listener(this)
                .<Design, FileDetails>chunk(chunkSize)
                .reader(dsItemReader)
                .processor(designItemProcessor)
                .writer(itemWriter)
                .taskExecutor(taskExecutor()).throttleLimit(throttleLimit)
                .build();
    }

    @Bean
    @Scope("prototype")
    public OpenDataItemWriter openDataItemWriter(@Autowired Archiver archiver,
                                                 @Autowired PackageUploader packageUploader) {
        return new OpenDataItemWriter(archiver, packageUploader);
    }

}
