package dropboks.dao;

import dropboks.model.DirectoryFileContest;
import org.jooq.TableField;
import org.jooq.impl.TableImpl;
import pl.edu.agh.kis.florist.db.tables.records.FolderFileContentsRecord;

/**
 * Created by miwas on 11.01.17.
 */
public class DirectoryFileContestDAO extends DAO<DirectoryFileContest, FolderFileContentsRecord> {


    public DirectoryFileContestDAO(Class<DirectoryFileContest> type, TableImpl<FolderFileContentsRecord> table) {
        super(type, table);
    }

    @Override
    public TableField<FolderFileContentsRecord, Integer> getIdOfTableRecord() {
        return null;
    }

    @Override
    public TableField<FolderFileContentsRecord, String> getNameIdOfTableRecord() {
        return null;
    }

    @Override
    public Integer getIdOfModel(DirectoryFileContest object) {
        return null;
    }
}
