package lt.gov.vpb.pdb.springbatch.opendata.file;

import lombok.Getter;
import org.springframework.util.Assert;

/**
 * Contains all information needed to create a file: file name and it's content as a byte array
 */
@Getter
public class FileDetails {
    /**
     * File name - should contain extension
     */
    private String fileName;
    private byte[] content;
    private ValidationResult validationResult;

    //Used by Archiver (zip)
    public FileDetails(String fileName, byte[] content) {
        Assert.notNull(fileName);
        Assert.notNull(content);

        this.fileName = fileName;
        this.content = content;
    }

    //Used by JAXB serializer (xml)
    public FileDetails(String fileName, byte[] content, ValidationResult validationResult) {
        Assert.notNull(fileName);
        Assert.notNull(content);

        this.fileName = fileName;
        this.content = content;
        this.validationResult = validationResult;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getContent() {
        return content;
    }
}
