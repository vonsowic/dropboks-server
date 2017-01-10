package dropboks.dao;

import dropboks.model.FileContent;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import pl.edu.agh.kis.florist.db.tables.pojos.FileMetadata;
import pl.edu.agh.kis.florist.db.tables.records.FileContentsRecord;
import pl.edu.agh.kis.florist.db.tables.records.FileMetadataRecord;

import java.util.List;

import static pl.edu.agh.kis.florist.db.tables.FileMetadata.FILE_METADATA;
import static pl.edu.agh.kis.florist.db.tables.FileContents.FILE_CONTENTS;


/**
 * Created by miwas on 10.01.17.
 */
public class FileDAO {

    private final String DB_URL = "jdbc:sqlite:test.db";

    public FileMetadata store(FileMetadata metadata, FileContent content) {
        try (DSLContext create = DSL.using(DB_URL)) {
            FileMetadataRecord record = create.newRecord(FILE_METADATA, metadata);
            FileContentsRecord record2 = create.newRecord(FILE_CONTENTS, content);
            record.store();
            record2.store();
            return record.into(FileMetadata.class);
        }
    }

    public Integer generateId(){
        try (DSLContext create = DSL.using(DB_URL)) {
            List<FileMetadata> list =
                    create.select(FILE_METADATA.fields())
                            .from(FILE_METADATA)
                            .fetchInto(FileMetadata.class);
            return new Integer(list.size());
        }
    }
}
