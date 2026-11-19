package lt.gov.vpb.pdb.springbatch.opendata.file.upload;

import lt.gov.vpb.pdb.springbatch.opendata.file.FileDetails;

public interface PackageUploader {

	void upload(FileDetails fileDetails, boolean overwrite);

}
