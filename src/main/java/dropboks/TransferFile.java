package dropboks;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Created by miwas on 10.01.17.
 */
public class TransferFile {

    private String fileContent;

    public TransferFile(String fileContent) {
        this.fileContent = fileContent;
    }

    public TransferFile(byte[] fileContent){
        this.fileContent = new String(fileContent);
    }

    public byte[] getBytes() {
        return fileContent.getBytes(StandardCharsets.UTF_8);
    }

    public byte[] encode(){
        return Base64.getEncoder().encode(getBytes());
    }

    public byte[] decode(){
        return Base64.getDecoder().decode(getBytes());
    }

    public Integer size(){
        return new Integer(this.getBytes().length);
    }

    public String bytesToString(){
        return fileContent;
    }
}
