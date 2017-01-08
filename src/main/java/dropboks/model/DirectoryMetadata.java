package dropboks.model;

import pl.edu.agh.kis.florist.db.tables.pojos.FolderMetadata;

/**
 * Created by miwas on 08.01.17.
 */
public class DirectoryMetadata extends FolderMetadata {
    public DirectoryMetadata(FolderMetadata value) {
        super(value);
    }

    public DirectoryMetadata(Integer folderId, String name, String pathLower, String pathDisplay, Integer parentFolderId, String serverCreatedAt, Integer ownerId) {
        super(folderId, name, pathLower, pathDisplay, parentFolderId, serverCreatedAt, ownerId);
    }

    public DirectoryMetadata(Integer folderId, String name, String serverCreatedAt, Integer ownerId) {
        super(folderId, name, null, null, null, serverCreatedAt, ownerId);
    }

    public DirectoryMetadata(String name, String serverCreatedAt, Integer ownerId) {
        super(null, name, null, null, null, serverCreatedAt, ownerId);
    }
}
