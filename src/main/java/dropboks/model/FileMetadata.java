package dropboks.model;

import dropboks.TransferFile;

import java.sql.Timestamp;

/**
 * Created by miwas on 10.01.17.
 */
public class FileMetadata extends pl.edu.agh.kis.florist.db.tables.pojos.FileMetadata{

    private TransferFile file;


    public FileMetadata(pl.edu.agh.kis.florist.db.tables.pojos.FileMetadata value) {
        super(value);
    }

    public FileMetadata(Integer fileId, String name, String pathLower, String pathDisplay, Integer size, Timestamp serverCreatedAt, Timestamp serverChangedAt, Integer enclosingFolderId) {
        super(fileId, name, pathLower, pathDisplay, size, serverCreatedAt, serverChangedAt, enclosingFolderId);
    }

    public FileMetadata(String name, String pathLower, String pathDisplay, Integer size, Timestamp serverCreatedAt, Timestamp serverChangedAt, Integer enclosingFolderId) {
        super(null, name, pathLower, pathDisplay, size, serverCreatedAt, serverChangedAt, enclosingFolderId);
    }




    public FileMetadata(pl.edu.agh.kis.florist.db.tables.pojos.FileMetadata value, TransferFile file) {
        super(value);
        this.file = file;
    }

    public FileMetadata getMetadataWithFile(TransferFile file){
        return new FileMetadata(this, file);
    }
}
