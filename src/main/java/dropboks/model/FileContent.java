package dropboks.model;

import pl.edu.agh.kis.florist.db.tables.pojos.FileContents;

/**
 * Created by miwas on 10.01.17.
 */
public class FileContent extends FileContents {
    public FileContent(FileContents value) {
        super(value);
    }

    public FileContent(Integer fileId, byte[] contents) {
        super(fileId, contents);
    }

    public FileContent(byte[] contents) {
        super(null, contents);
    }

}
