package dropboks.dao;

import dropboks.model.DirectoryMetadata;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import pl.edu.agh.kis.florist.db.tables.records.FolderMetadataRecord;

import java.util.List;

import static pl.edu.agh.kis.florist.db.tables.FolderMetadata.FOLDER_METADATA;

/**
 * Directory Data Access Object
 * @author miwas
 */
public class DirectoryMetadataDAO {
    private final String DB_URL = "jdbc:sqlite:test.db";

    public DirectoryMetadata findUsersDirectoryByHisId(int id){
        try (DSLContext create = DSL.using(DB_URL)) {
            FolderMetadataRecord record = null;
            record = create.selectFrom(FOLDER_METADATA).where(FOLDER_METADATA.OWNER_ID.equal(id)).fetchOne();
            DirectoryMetadata directoryMetadata = record.into(DirectoryMetadata.class);
            return directoryMetadata;
        }
    }

    public DirectoryMetadata store(DirectoryMetadata directoryMetadata){
        try (DSLContext create = DSL.using(DB_URL)) {
            FolderMetadataRecord record = create.newRecord(FOLDER_METADATA, directoryMetadata);
            record.store();
            return record.into(DirectoryMetadata.class);
        }
    }

    public DirectoryMetadata loadDirOfId(int dirId) {
        try (DSLContext create = DSL.using(DB_URL)) {
            FolderMetadataRecord record = create.selectFrom(FOLDER_METADATA).where(FOLDER_METADATA.FOLDER_ID.equal(dirId)).fetchOne();
            DirectoryMetadata directory = record.into(DirectoryMetadata.class);
            return directory;
        }
    }

    public Integer generateId(){
        try (DSLContext create = DSL.using(DB_URL)) {
            List<DirectoryMetadata> list =
                    create.select(FOLDER_METADATA.fields())
                            .from(FOLDER_METADATA)
                            .fetchInto(DirectoryMetadata.class);
            return new Integer(list.size());
        }
    }


}
