package dropboks.dao;

import dropboks.model.FileContent;
import org.jooq.TableField;
import org.jooq.impl.TableImpl;
import pl.edu.agh.kis.florist.db.tables.records.FileContentsRecord;

import static pl.edu.agh.kis.florist.db.tables.FileContents.FILE_CONTENTS;

/**
 * Created by miwas on 11.01.17.
 */
public class FileContentDAO extends DAO<FileContent, FileContentsRecord, byte[]> {
    public FileContentDAO(Class<FileContent> type, TableImpl<FileContentsRecord> table) {
        super(type, table);
    }

    @Override
    public TableField<FileContentsRecord, Integer> getIdOfTableRecord() {
        return FILE_CONTENTS.FILE_ID;
    }

    @Override
    public TableField<FileContentsRecord, byte[]> getSecondIdOfTableRecord() {
        return FILE_CONTENTS.CONTENTS;
    }

    @Override
    protected Integer getId(FileContent object) {
        return object.getFileId();
    }
}
