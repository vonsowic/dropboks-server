package dropboks.dao;

import dropboks.DropboksController;
import dropboks.PathResolver;
import dropboks.model.FileMetadata;
import org.jooq.TableField;
import org.jooq.impl.TableImpl;
import pl.edu.agh.kis.florist.db.tables.records.FileMetadataRecord;
import static pl.edu.agh.kis.florist.db.tables.FileMetadata.FILE_METADATA;


/**
 * Created by miwas on 10.01.17.
 */
public class FileMetadataDAO extends MetadataDAO<FileMetadata, FileMetadataRecord, String> {

    private DropboksController controller;

    public FileMetadataDAO(Class<FileMetadata> type, TableImpl<FileMetadataRecord> table, DropboksController controller) {
        super(type, table);
        this.controller = controller;
    }

    @Override
    public TableField<FileMetadataRecord, Integer> getIdOfTableRecord() {
        return FILE_METADATA.FILE_ID;
    }

    @Override
    public TableField<FileMetadataRecord, String> getSecondIdOfTableRecord() {
        return FILE_METADATA.PATH_DISPLAY;
    }

    @Override
    public FileMetadata move(String path, String newPath) {
        FileMetadata record = this.findBySecondId(path);
        FileMetadata newFile = this.findBySecondId(PathResolver.getParentPath(newPath));

        Integer idOfDirectory = controller
                .getDirectoryMetadataRepository()
                .getIdBySecondId(PathResolver.getParentPath(newPath));

        FileMetadata newRecord = new FileMetadata(
                record.getFileId(),
                PathResolver.getName(newPath),
                newPath.toLowerCase(),
                newPath,
                idOfDirectory,
                record.getSize(),
                record.getServerCreatedAt(),
                controller.getServerName(),
                record.getOwnerId()
        );

        //controller.getDirectoryFileContentRepository()

        this.update(newRecord);
        return newRecord;
    }

    @Override
    public FileMetadata getMetaData(String path) {
        return findBySecondId(path);
    }

    @Override
    public FileMetadata rename(String oldName, String newName) {
        FileMetadata fileMetadata = findBySecondId(oldName);
        FileMetadata newFileMetadata = new FileMetadata(
                fileMetadata.getFileId(),
                PathResolver.getName(newName),
                fileMetadata.getPathLower(),
                fileMetadata.getPathDisplay(),
                fileMetadata.getEnclosingFolderId(),
                fileMetadata.getSize(),
                fileMetadata.getServerCreatedAt(),
                DropboksController.getServerName(),
                fileMetadata.getOwnerId()
        );

        update(newFileMetadata);
        return newFileMetadata;
    }


    @Override
    protected Integer getId(FileMetadata object) {
        return object.getFileId();
    }
}
