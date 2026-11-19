package lt.gov.vpb.pdb.springbatch.opendata;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.util.Assert;

import static lt.gov.vpb.pdb.springbatch.opendata.ExportType.MONTHLY;

@Getter
public class OpenDataExportCommand extends ApplicationEvent {

    private final ExportType exportType;

    private final Integer year;

    private final Integer month;

    private OpenDataExportCommand(Object source, ExportType exportType, Integer year, Integer month) {
        super(source);
        Assert.notNull(exportType, "syncType must not be null");
        this.exportType = exportType;
        this.year = year;
        this.month = month;
    }

    public static OpenDataExportCommand full(Object source) {
        return new OpenDataExportCommand(source, ExportType.FULL, null, null);
    }

    public static OpenDataExportCommand monthly(Object source) {
        return new OpenDataExportCommand(source, MONTHLY, null, null);
    }

    public static OpenDataExportCommand monthly(Object source, Integer year, Integer month) {
        return new OpenDataExportCommand(source, MONTHLY, year, month);
    }
}
