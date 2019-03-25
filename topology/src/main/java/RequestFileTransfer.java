import java.io.Serializable;

public class RequestFileTransfer implements Serializable {

    public final String fileName;
    public int sectionId;

    public RequestFileTransfer(String fileName, int sectionId) {
        this.fileName = fileName;
        this.sectionId = sectionId;
    }

}
