package dropboks.model;

import dropboks.TransferFile;

/**
 * Created by miwas on 10.01.17.
 */
public class FileMetadata extends pl.edu.agh.kis.florist.db.tables.pojos.FileMetadata{

    private TransferFile file;


    public FileMetadata(pl.edu.agh.kis.florist.db.tables.pojos.FileMetadata value) {
        super(value);
    }

    public FileMetadata(Integer fileId, String name, String pathLower, String pathDisplay, Integer enclosingFolderId, Integer size, String serverCreatedAt, String serverChangedAt, Integer ownerId) {
        super(fileId, name, pathLower, pathDisplay, enclosingFolderId, size, serverCreatedAt, serverChangedAt, ownerId);
    }

    public FileMetadata(String name, String pathLower, String pathDisplay, Integer enclosingFolderId, Integer size, String serverCreatedAt, Integer ownerId) {
        super(null, name, pathLower, pathDisplay, enclosingFolderId, size, serverCreatedAt, null, ownerId);
    }

    public FileMetadata(Integer id, String name, String pathLower, String pathDisplay, Integer enclosingFolderId, Integer size, String serverCreatedAt, Integer ownerId) {
        super(id, name, pathLower, pathDisplay, enclosingFolderId, size, serverCreatedAt, null, ownerId);
    }

    public FileMetadata(pl.edu.agh.kis.florist.db.tables.pojos.FileMetadata value, TransferFile file) {
        super(value);
        this.file = file;
    }

    public FileMetadata getMetadataWithFile(TransferFile file){
        return new FileMetadata(this, file);
    }

}
