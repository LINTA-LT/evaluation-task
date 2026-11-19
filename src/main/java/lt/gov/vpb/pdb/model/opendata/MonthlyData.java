package lt.gov.vpb.pdb.model.opendata;

import lombok.Data;

import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;

@Data
public class MonthlyData {

    private String yearMonth;
    private Set<Path> designFiles = new TreeSet<>();
    private Set<Path> trademarkFiles = new TreeSet<>();

}
