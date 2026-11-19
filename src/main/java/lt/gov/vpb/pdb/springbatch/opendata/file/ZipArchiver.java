package lt.gov.vpb.pdb.springbatch.opendata.file;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * {@link Archiver} creating ZIP files
 *
 */
@Component
public class ZipArchiver implements Archiver {

	@Override
	public FileDetails createArchive(String fileName, List<FileDetails> files) {
		Assert.notNull(fileName, "fileName cannot be empty");
		Assert.notNull(files, "files cannot be null");
		Assert.notEmpty(files, "files collection cannot be empty");

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ZipOutputStream zipfile = new ZipOutputStream(bos);

		try {
			for (FileDetails fileDetails : files) {
				ZipEntry zipentry = new ZipEntry(fileDetails.getFileName());
				zipfile.putNextEntry(zipentry);
				zipfile.write(fileDetails.getContent());
			}
			zipfile.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return new FileDetails(fileName, bos.toByteArray());
	}

	public FileDetails createEmptyArchive(String archiveFileName, String emptyFileName) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ZipOutputStream zipfile = new ZipOutputStream(bos);
		try {
			// generate the filename for the empty xml and add it in the zip
			ZipEntry zipentry = new ZipEntry(emptyFileName);
			zipfile.putNextEntry(zipentry);
			zipfile.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return new FileDetails(archiveFileName, bos.toByteArray());
	}
}
