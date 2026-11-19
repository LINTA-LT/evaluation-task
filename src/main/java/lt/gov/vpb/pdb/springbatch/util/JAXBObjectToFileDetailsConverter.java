package lt.gov.vpb.pdb.springbatch.util;

import lt.gov.vpb.pdb.springbatch.opendata.file.FileDetails;
import lt.gov.vpb.pdb.springbatch.opendata.file.FileDetailsCreator;
import lt.gov.vpb.pdb.springbatch.opendata.file.FileNameGenerator;
import org.springframework.util.Assert;

/**
 * Converts generic object T to {@link FileDetails}
 *
 */
public class JAXBObjectToFileDetailsConverter<T> {
	
	private final FileDetailsCreator fileDetailsCreator;
	
	private final FileNameGenerator<T> fileNameGenerator;

	public JAXBObjectToFileDetailsConverter(FileDetailsCreator fileDetailsCreator,
			FileNameGenerator<T> fileNameGenerator) {
		this.fileDetailsCreator = fileDetailsCreator;
		this.fileNameGenerator = fileNameGenerator;
	}

	public FileDetails convert(T object, String fileNameDatePrefix) {
		Assert.notNull(object);
		Assert.notNull(fileNameDatePrefix);

		String filename = fileNameGenerator.generateFileName(object, fileNameDatePrefix);
		FileDetails details = fileDetailsCreator.create(object, filename);
		return details;
	}

}
