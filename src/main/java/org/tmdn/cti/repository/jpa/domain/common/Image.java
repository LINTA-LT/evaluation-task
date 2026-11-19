package org.tmdn.cti.repository.jpa.domain.common;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.math.BigInteger;

@MappedSuperclass
@Data
public class Image extends BaseEntity {

    public static final String THUMBNAIL_SUFFIX = "-thumb";

    @Column(name = "FILENAME")
    private String filename;

    @Column(name = "PREFEREDINDICATOR")
    private BigInteger preferedIndicator;
    
    @Transient
    public String getThumbnailFileName() {
        return filename + THUMBNAIL_SUFFIX;
    }

    /**
     * Gets file extension, for example for filename avatar.jpg returns jpg.
     * Recognizes also thumbnails in the way that if thumbnail file name is avatar.jpg-thumb returns extension jpg
     *
     * @return file extension
     */
    public String getFileExtension() {
        try {
            String extension = filename.substring(filename.lastIndexOf(".") + 1);

            if (extension.endsWith(THUMBNAIL_SUFFIX)) {
                extension = extension.replace(THUMBNAIL_SUFFIX, "");
            }

            return extension;
        } catch (Exception e) {
            throw new RuntimeException("Cannot recognize file extension", e);
        }
    }

}
