package lt.gov.vpb.pdb.springbatch.opendata.file;

import lt.gov.vpb.pdb.springbatch.util.JAXBSerializer;

public class FileDetailsCreator<T> {

    private final JAXBSerializer<T> jaxbSerializer;

    public FileDetailsCreator(JAXBSerializer<T> jaxbSerializer) {
        this.jaxbSerializer = jaxbSerializer;
    }

    /**
     * Creates File Details for given object. File details content is byte array of serialized JAXB class
     *
     * @param object any object that can be serialized with JAXB
     * @param fileName target file filename
     * @return generated FileDetails
     */
    public FileDetails create(Object object, String fileName) {
        byte[] serializedTransaction = jaxbSerializer.serialize(object);
        ValidationResult result = jaxbSerializer.getResult();
        if(serializedTransaction.length==0) {
            return null;
        }

        return new FileDetails(fileName, serializedTransaction, result);
    }


}
