import java.io.Serializable;

public class FileTransferReady implements Serializable {

    public final String fileName;
    public final int sectionId;

    public FileTransferReady(String fileName, int sectionId) {
        this.fileName = fileName;
        this.sectionId = sectionId;
    }
}
