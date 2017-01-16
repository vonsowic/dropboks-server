package dropboks.dao;

import dropboks.model.DirectoryDirectoryContest;
import dropboks.model.DirectoryMetadata;
import org.jooq.Table;
import org.jooq.TableField;
import pl.edu.agh.kis.florist.db.tables.records.FolderFolderContentsRecord;

import java.util.List;

import static pl.edu.agh.kis.florist.db.Tables.FOLDER_FOLDER_CONTENTS;


/**
 * Created by miwas on 12.01.17.
 */
public class DirectoryDirectoryContenstDAO extends ContentsDAO<DirectoryDirectoryContest, FolderFolderContentsRecord> {

    public DirectoryDirectoryContenstDAO(Class type, Table table, DirectoryMetadataDAO directoryMetadata) {
        super(type, table, directoryMetadata);
    }

    @Override
    public TableField<FolderFolderContentsRecord, Integer> getSecondIdOfTableRecord() {
        return FOLDER_FOLDER_CONTENTS.CONTAINED_FOLDER_ID;
    }

    @Override
    public TableField<FolderFolderContentsRecord, Integer> getIdOfTableRecord() {
        return FOLDER_FOLDER_CONTENTS.PARENT_FOLDER_ID;
    }

    @Override
    public Integer getSecondId(DirectoryDirectoryContest object) {
        return object.getContainedFolderId();
    }

    @Override
    public Integer getId(DirectoryDirectoryContest object) {
        return object.getParentFolderId();
    }

    @Override
    public DirectoryDirectoryContest create(Integer firstKey, Integer secondKey) {
        return new DirectoryDirectoryContest(firstKey, secondKey);
    }
}
