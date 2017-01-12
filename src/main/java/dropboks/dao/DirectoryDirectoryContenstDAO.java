package dropboks.dao;

import dropboks.model.DirectoryDirectoryContest;
import org.jooq.Table;
import org.jooq.TableField;
import pl.edu.agh.kis.florist.db.tables.records.FolderFolderContentsRecord;
import static pl.edu.agh.kis.florist.db.Tables.FOLDER_FOLDER_CONTENTS;


/**
 * Created by miwas on 12.01.17.
 */
public class DirectoryDirectoryContenstDAO extends ContentsDAO<DirectoryDirectoryContest, FolderFolderContentsRecord, Integer> {
    protected DirectoryDirectoryContenstDAO(Class type, Table table) {
        super(type, table);
    }

    @Override
    public TableField<FolderFolderContentsRecord, Integer> getIdOfTableRecord() {
        return FOLDER_FOLDER_CONTENTS.CONTAINED_FOLDER_ID;
    }

    @Override
    public TableField<FolderFolderContentsRecord, Integer> getSecondIdOfTableRecord() {
        return FOLDER_FOLDER_CONTENTS.PARENT_FOLDER_ID;
    }

    @Override
    protected Integer getId(DirectoryDirectoryContest object) {
        return object.getContainedFolderId();
    }

}
