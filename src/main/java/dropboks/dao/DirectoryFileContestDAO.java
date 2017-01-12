package dropboks.dao;

import dropboks.model.DirectoryFileContest;
import org.jooq.TableField;
import org.jooq.impl.TableImpl;
import pl.edu.agh.kis.florist.db.tables.records.FolderFileContentsRecord;
import static pl.edu.agh.kis.florist.db.tables.FolderFileContents.FOLDER_FILE_CONTENTS;

/**
 * Created by miwas on 11.01.17.
 */
public class DirectoryFileContestDAO extends ContentsDAO<DirectoryFileContest, FolderFileContentsRecord, Integer> {

    public DirectoryFileContestDAO(Class<DirectoryFileContest> type, TableImpl<FolderFileContentsRecord> table) {
        super(type, table);
    }

    @Override
    public TableField<FolderFileContentsRecord, Integer> getIdOfTableRecord() {
        return FOLDER_FILE_CONTENTS.CONTAINED_FILE_ID;
    }

    @Override
    public TableField<FolderFileContentsRecord, Integer> getSecondIdOfTableRecord() {
        return FOLDER_FILE_CONTENTS.PARENT_FOLDER_ID;
    }

    @Override
    public Integer getId(DirectoryFileContest object) {
        return object.getContainedFileId();
    }


}
