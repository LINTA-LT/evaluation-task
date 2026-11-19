package lt.gov.vpb.pdb.springbatch.opendata.file;

import java.util.List;

/**
 * Tool used to handle creating archives.
 *
 */
public interface Archiver {

	/**
	 * Creates an archive containing given files
	 *
	 * @param fileName - created archive file name
	 * @param files    - files that are put into archive
	 * @return FileDetails containing archiveArchiver
	 */
	FileDetails createArchive(String fileName, List<FileDetails> files);

}
