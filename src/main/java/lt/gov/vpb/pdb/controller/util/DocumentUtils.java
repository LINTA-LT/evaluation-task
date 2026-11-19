package lt.gov.vpb.pdb.controller.util;

public class DocumentUtils {
    public static String getContentType(String fileExtension) {
        fileExtension = fileExtension.toLowerCase();
        switch (fileExtension) {
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "pdf":
                return "application/pdf";
            case "zip":
                return "application/zip";
            default:
                return "text/html";
        }
    }
}
