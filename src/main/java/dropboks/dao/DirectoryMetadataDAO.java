package dropboks.dao;

import dropboks.DropboksController;
import dropboks.PathResolver;
import dropboks.model.DirectoryMetadata;
import dropboks.model.User;
import org.jooq.TableField;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.TableImpl;
import pl.edu.agh.kis.florist.db.tables.records.FolderMetadataRecord;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static pl.edu.agh.kis.florist.db.tables.FolderMetadata.FOLDER_METADATA;

/**
 * Directory Data Access Object
 * @author miwas
 */
public class DirectoryMetadataDAO extends MetadataDAO<DirectoryMetadata, FolderMetadataRecord> {

    public DirectoryMetadataDAO(Class<DirectoryMetadata> type,
                                TableImpl<FolderMetadataRecord> table,
                                DropboksController controller) {
        super(type, table, controller);
    }


    @Override
    public TableField<FolderMetadataRecord, Integer> getIdOfTableRecord() {
        return FOLDER_METADATA.FOLDER_ID;
    }

    @Override
    public TableField<FolderMetadataRecord, String> getSecondIdOfTableRecord() {
        return FOLDER_METADATA.PATH_DISPLAY;
    }

    @Override
    public TableField<FolderMetadataRecord, Integer> getParentIdTableRecord() {
        return FOLDER_METADATA.PARENT_FOLDER_ID;
    }

    @Override
    protected Integer getId(DirectoryMetadata object) {
        return object.getFolderId();
    }

    @Override
    public DirectoryMetadata move(String path, String newPath) {
        DirectoryMetadata record = this.findBySecondId(path);
        DirectoryMetadata newDirectory = this.findBySecondId(PathResolver.getParentPath(newPath));

        DirectoryMetadata newRecord = new DirectoryMetadata(
                record.getFolderId(),
                PathResolver.getName(newPath),
                newPath.toLowerCase(),
                newPath,
                newDirectory.getFolderId(),
                time()
        );

        this.update(newRecord);
        return newRecord;
    }

    @Override
    public DirectoryMetadata getMetaData(String path) {
        return findBySecondId(path);
    }

    // TODO : recursive
    @Override
    public DirectoryMetadata rename(String oldName, String newName) {
        DirectoryMetadata directoryMetadata = findBySecondId(oldName);
        directoryMetadata.setName(newName);

        update(directoryMetadata);
        return directoryMetadata;
    }

    // adds directory to existing directory
    public DirectoryMetadata store(String path) throws DataAccessException {
        DirectoryMetadata newDirectory ;

        try {
            String name = PathResolver.getUserName(path);
            //User user = getController().getUsersRepository().findBySecondId(name);

            newDirectory = new DirectoryMetadata(
                    PathResolver.getName(path),
                    path.toLowerCase(),
                    path,
                    findBySecondId(PathResolver.getParentPath(path)).getFolderId(),
                    time()
            );
        } catch (DataAccessException e){
            throw e;
        }
        return store( newDirectory );
    }

    public DirectoryMetadata createDirectoryForUser(User user) {
        DirectoryMetadata usersDirectory =
                new DirectoryMetadata(
                        user.getUserName(),
                        user.getUserName().toLowerCase(),
                        user.getUserName(),
                        0,  // root directory
                        time());

        DirectoryMetadata result = null;
        try {
           this.insert(usersDirectory);
        } catch (DataAccessException e){
            throw e;
        }
        return result;
    }
}
