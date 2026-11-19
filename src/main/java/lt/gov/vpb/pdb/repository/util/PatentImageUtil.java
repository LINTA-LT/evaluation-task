package lt.gov.vpb.pdb.repository.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.im4java.process.ProcessStarter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
@Slf4j
public class PatentImageUtil {

	@Value("${patent.images-dir}")
	private String directory;

	@Value("${im4java.toolpath:@null}")
	String im4javaToolpath;


	@PostConstruct
	public void postConstruct(){
		if(isNotBlank(im4javaToolpath)){
			ProcessStarter.setGlobalSearchPath(im4javaToolpath);
		}
	}

	public byte[] getJpegImage(String patentDir, String fileName){
		String jpegFilePath = getPath(patentDir, FilenameUtils.getBaseName(fileName) + FilenameUtils.EXTENSION_SEPARATOR_STR + "jpg");
		File file = new File(jpegFilePath);
		if(file.exists()){
			try(FileInputStream fis = new FileInputStream(file)){
				return IOUtils.toByteArray(fis);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}

		} else {
			return convertToJpeg(patentDir, fileName);
		}
		return null;
	}

	/**
	 * Converts one image in other in JPG format
	 *
	 * @param fileName
	 *            String with the full path to the source image
	 * @return byte[] with the bytes for the converted image
	 */
	private byte[] convertToJpeg(String patentDir, String fileName) {
		return convert(patentDir, fileName, "jpg");
	}

	/**
	 * Converts one image in other in format
	 *
	 * @param fileName
	 *            String with the full path to the source image
	 * @param format
	 *            String with the format to convert to
	 * @return byte[] with the bytes for the converted image
	 */
	private byte[] convert(String patentDir, String fileName, String format) {

		byte[] convertedArray = null;

		String convertedFilePath = getPath(patentDir, FilenameUtils.getBaseName(fileName) + FilenameUtils.EXTENSION_SEPARATOR_STR + format);

		try {

			IMOperation op = new IMOperation();
			op.addImage(getPath(patentDir, fileName));
			op.addImage(convertedFilePath);

			// set up command
			ConvertCmd cmd = new ConvertCmd();
			// run command and extract BufferedImage from OutputConsumer
			cmd.run(op);

			FileInputStream fis = new FileInputStream(convertedFilePath);
			convertedArray = IOUtils.toByteArray(fis);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			log.error("Cannot convert image. The reason: {}", e.getMessage());
		} finally {
			try {
				//Files.delete(get(tmpFilePath));
			} catch(Exception e) {
				log.error("Could not delete file {}", convertedFilePath);
			}
		}

		return convertedArray;
	}

	/**
	 * Returns the full path to the image
	 * @param fileName image file name
	 * @return an String containing the image full path
	 */
	private String getPath(String patentDir, String fileName) {
		if(!directory.endsWith(File.separator)){
			directory += File.separator;
		}
		return directory + patentDir + File.separator + fileName;
	}

}
