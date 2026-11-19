package lt.gov.vpb.pdb.springbatch.opendata;

import lt.gov.vpb.pdb.springbatch.opendata.file.Archiver;
import lt.gov.vpb.pdb.springbatch.opendata.file.FileDetails;
import lt.gov.vpb.pdb.springbatch.opendata.file.upload.PackageUploader;
import org.springframework.batch.item.ItemWriter;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.leftPad;

public class OpenDataItemWriter implements ItemWriter<FileDetails> {

	public static final int NUM_FILES_PER_ARCHIVE = 1000;

	private String fileNameDatePrefix;

	private Archiver archiver;

	private PackageUploader packageUploader;

	List<FileDetails> fileDetailsToWrite = new ArrayList<>();

	private int archiveSeqNum = 1;

	public OpenDataItemWriter(Archiver archiver, PackageUploader packageUploader){
		this.archiver = archiver;
		this.packageUploader = packageUploader;
	}

	@Override
	public void write(List<? extends FileDetails> items) throws Exception {

		List<FileDetails> fileDetailsList = new ArrayList<>(items);
		fileDetailsToWrite.addAll(fileDetailsList);
		if (fileDetailsToWrite.size() >= NUM_FILES_PER_ARCHIVE) {
			createArchive(fileDetailsToWrite.subList(0, NUM_FILES_PER_ARCHIVE));
			List<FileDetails> tmpList = fileDetailsToWrite.subList(NUM_FILES_PER_ARCHIVE, fileDetailsToWrite.size());
			fileDetailsToWrite = tmpList;
		}
		// write rest of data to the file
		if (!fileDetailsToWrite.isEmpty()) {
			createArchive(fileDetailsToWrite);
			fileDetailsToWrite.clear();
		}
	}

	public void createArchive(List<FileDetails> files) {
		FileDetails fileDetails = archiver.createArchive(getArchiveFileName(), files);
		packageUploader.upload(fileDetails, true);
	}

	private String getArchiveFileName() {
		String fileName = fileNameDatePrefix + leftPad(String.valueOf(archiveSeqNum++), 4, "0") + ".zip";
		return fileName;

	}

	public void setFileNameDatePrefix(String fileNameDatePrefix) {
		this.fileNameDatePrefix = fileNameDatePrefix;
	}

}
