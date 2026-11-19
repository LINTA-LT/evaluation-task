package lt.gov.vpb.pdb.springbatch.opendata.file.upload;

import lombok.extern.slf4j.Slf4j;
import lt.gov.vpb.pdb.springbatch.opendata.file.FileDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Uploader implementation that saves file in local file system
 *
 */
@Component
@Slf4j
public class FileSystemPackageUploader implements PackageUploader {

	@Value("${opendata.files-dir}")
	private String targetDirectory;

	@Override
	public void upload(FileDetails fileDetails, boolean overwrite) {
		try {
			verifyCreateTargetDirectory();
			if (overwrite || !exist(fileDetails)) {
				FileOutputStream fos = new FileOutputStream(targetDirectory + "/" + fileDetails.getFileName());
				fos.write(fileDetails.getContent());
				fos.close();
				log.info("Successful copy of file {} to local directory {}", fileDetails.getFileName(), targetDirectory);
			} else {
				log.info("File {} already exists in local directory {}", fileDetails.getFileName(), targetDirectory);
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void verifyCreateTargetDirectory() throws IOException {
		File f = new File(targetDirectory);
		if(!f.exists())
			f.mkdirs();
	}

	private Boolean exist(FileDetails fileDetails) {

		if (fileDetails.getContent() != null) {
			try {

				File file = new File(targetDirectory + "/" + fileDetails.getFileName());
				if (file.isFile() && file.length() == fileDetails.getContent().length) {
					return Boolean.TRUE;
				}

			} catch (Exception e) {
				// Do nothing;
			}
		}

		return Boolean.FALSE;
	}

}
