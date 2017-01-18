package dropboks.dao;

import dropboks.DropboksController;
import dropboks.PathResolver;
import dropboks.TransferFile;
import dropboks.model.FileMetadata;
import org.jooq.TableField;
import org.jooq.impl.TableImpl;
import pl.edu.agh.kis.florist.db.tables.records.FileMetadataRecord;

import java.util.List;

import static pl.edu.agh.kis.florist.db.tables.FileMetadata.FILE_METADATA;


/**
 * Created by miwas on 10.01.17.
 */
public class FileMetadataDAO extends MetadataDAO<FileMetadata, FileMetadataRecord> {

    public FileMetadataDAO(Class<FileMetadata> type, TableImpl<FileMetadataRecord> table, DropboksController controller) {
        super(type, table, controller);
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
    public TableField<FileMetadataRecord, Integer> getParentIdTableRecord() {
        return FILE_METADATA.ENCLOSING_FOLDER_ID;
    }

    // TODO : recursive
    @Override
    public FileMetadata move(String path, String newPath) {
        FileMetadata record = this.findBySecondId(path);

        Integer idOfDirectory = getController()
                .getDirectoryMetadataRepository()
                .getIdBySecondId(PathResolver.getParentPath(newPath));


        return record;
    }

    @Override
    public FileMetadata getMetaData(String path) {
        return findBySecondId(path);
    }

    @Override
    public FileMetadata rename(String oldName, String newName) {
        FileMetadata fileMetadata = findBySecondId(oldName);


        update(fileMetadata);
        return fileMetadata;
    }

    @Override
    protected Integer getId(FileMetadata object) {
        return object.getFileId();
    }

    public FileMetadata create(String pathToFile, TransferFile tmpFile) {
        return new FileMetadata(
                PathResolver.getUserName(pathToFile),
                pathToFile.toLowerCase(),
                pathToFile,
                tmpFile.size(),
                time(),
                null,
                getController().getDirectoryMetadataRepository().findBySecondId(PathResolver.getParentPath(pathToFile)).getFolderId()

        );

    }
}
