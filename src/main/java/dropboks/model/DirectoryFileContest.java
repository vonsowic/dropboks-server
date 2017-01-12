package dropboks.model;

import pl.edu.agh.kis.florist.db.tables.pojos.FolderFileContents;

/**
 * Created by miwas on 10.01.17.
 */
public class DirectoryFileContest extends FolderFileContents
        {

    public DirectoryFileContest(FolderFileContents value) {
        super(value);
    }

    public DirectoryFileContest(Integer parentFolderId, Integer containedFileId) {
        super(parentFolderId, containedFileId);
    }

}
