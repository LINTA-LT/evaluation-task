package lt.gov.vpb.pdb.springbatch.opendata.file;

public interface FileNameGenerator<T> {
	
	String generateFileName(T object, String fileNameDatePrefix);

}
