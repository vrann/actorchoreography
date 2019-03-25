import akka.stream.SourceRef;
import akka.util.ByteString;

import java.io.Serializable;

public class FileTransfer implements Serializable {
    final SourceRef<ByteString> sourceRef;
    final String fileName;

    public FileTransfer(
        String fileName,
        SourceRef<ByteString> sourceRef
    ) {
        this.sourceRef = sourceRef;
        this.fileName = fileName;
    }
}
