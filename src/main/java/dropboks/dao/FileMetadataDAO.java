package dropboks.dao;

import dropboks.model.FileMetadata;
import org.jooq.TableField;
import org.jooq.impl.TableImpl;
import pl.edu.agh.kis.florist.db.tables.records.FileMetadataRecord;
import static pl.edu.agh.kis.florist.db.tables.FileMetadata.FILE_METADATA;


/**
 * Created by miwas on 10.01.17.
 */
public class FileMetadataDAO extends DAO<FileMetadata, FileMetadataRecord> {

    private final String DB_URL = "jdbc:sqlite:test.db";

    public FileMetadataDAO(Class<FileMetadata> type, TableImpl<FileMetadataRecord> table) {
        super(type, table);
    }

    @Override
    public TableField<FileMetadataRecord, Integer> getIdOfTableRecord() {
        return FILE_METADATA.FILE_ID;
    }

    @Override
    public TableField<FileMetadataRecord, String> getNameIdOfTableRecord() {
        return FILE_METADATA.PATH_DISPLAY;
    }

    @Override
    public Integer getIdOfModel(FileMetadata object) {
        return object.getFileId();
    }
}
