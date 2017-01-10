package dropboks.model;

/**
 * Created by miwas on 10.01.17.
 */
public class FileMetadata extends pl.edu.agh.kis.florist.db.tables.pojos.FileMetadata{
    public FileMetadata(pl.edu.agh.kis.florist.db.tables.pojos.FileMetadata value) {
        super(value);
    }

    public FileMetadata(Integer fileId, String name, String pathLower, String pathDisplay, Integer enclosingFolderId, Integer size, String serverCreatedAt, String serverChangedAt, Integer ownerId) {
        super(fileId, name, pathLower, pathDisplay, enclosingFolderId, size, serverCreatedAt, serverChangedAt, ownerId);
    }

    public FileMetadata(String name, String pathLower, String pathDisplay, Integer enclosingFolderId, Integer size, String serverCreatedAt, Integer ownerId) {
        super(null, name, pathLower, pathDisplay, enclosingFolderId, size, serverCreatedAt, null, ownerId);
    }
}
