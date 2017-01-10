package dropboks;

import java.nio.charset.StandardCharsets;

/**
 * Created by miwas on 10.01.17.
 */
public class TransferFile {

    private String fileContent;

    public TransferFile(String fileContent) {
        this.fileContent = fileContent;
    }

    public String getFileContent() {
        return fileContent;
    }

    public byte[] getBytes(){
        return fileContent.getBytes(StandardCharsets.UTF_8);
    }

    public Integer size(){
        return new Integer(this.getBytes().length);
    }
}
