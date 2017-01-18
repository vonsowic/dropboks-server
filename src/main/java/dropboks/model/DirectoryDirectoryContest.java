package dropboks.model;

import pl.edu.agh.kis.florist.db.tables.pojos.FolderFolderContents;

/**
 * Created by miwas on 12.01.17.
 */
public class DirectoryDirectoryContest extends FolderFolderContents {
    public DirectoryDirectoryContest(FolderFolderContents value) {
        super(value);
    }

    public DirectoryDirectoryContest( Integer containedFolderId, Integer parentFolderId) {
        super(containedFolderId, parentFolderId);
    }
}
