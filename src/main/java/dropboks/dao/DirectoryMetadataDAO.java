package dropboks.dao;

import dropboks.DropboksController;
import dropboks.PathResolver;
import dropboks.model.DirectoryMetadata;
import dropboks.model.User;
import org.jooq.TableField;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.TableImpl;
import pl.edu.agh.kis.florist.db.tables.records.FolderMetadataRecord;

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
                record.getServerCreatedAt(),
                record.getOwnerId()
        );

        this.update(newRecord);
        return newRecord;
    }

    @Override
    public DirectoryMetadata getMetaData(String path) {
        return findBySecondId(path);
    }

    @Override
    public DirectoryMetadata rename(String oldName, String newName) {
        DirectoryMetadata directoryMetadata = findBySecondId(oldName);
        DirectoryMetadata newDirectoryMetadata = new DirectoryMetadata(
                directoryMetadata.getFolderId(),
                PathResolver.getName(newName),
                newName.toLowerCase(),
                newName,
                directoryMetadata.getParentFolderId(),
                directoryMetadata.getServerCreatedAt(),
                directoryMetadata.getOwnerId()
        );

        update(newDirectoryMetadata);
        return newDirectoryMetadata;
    }

    @Override
    public ContentsDAO getContestRepository() {
        return getController().getDirectoryDirectoryContestRepository();
    }

    @Override
    public DirectoryMetadata getMetadataWithChildren(Integer id, List<DirectoryMetadata> listOfChildren) {
        DirectoryMetadata directoryMetadata = findById(id);
        return directoryMetadata.appendChildren(listOfChildren);
    }

    // adds directory to existing directory
    public DirectoryMetadata store(String path) throws DataAccessException {
        DirectoryMetadata newDirectory ;
        ArrayList<String> tmpPath = new ArrayList<>(Arrays.asList(path.split("/")));

        try {
            String name = PathResolver.getUserName(path);
            User user = getController().getUsersRepository().findBySecondId(name);


            newDirectory = new DirectoryMetadata(
                    this.generateId(),
                    PathResolver.getName(path),
                    path.toLowerCase(),
                    path,
                    getParentDirecoryId(tmpPath),
                    getController().getServerName(),
                    user.getId()
            );
        } catch (DataAccessException e){
            throw e;
        }
        return store( newDirectory );
    }

    public Integer getParentDirecoryId(ArrayList<String> path){
        String tmp;
        if (path.size()>1){
            tmp = PathResolver.getParentPath(path);
        } else {
            return null;
        }
        Integer id = findBySecondId(tmp).getFolderId();
        return id;
    }

    public DirectoryMetadata createDirectoryForUser(User user) {
        DirectoryMetadata usersDirectory =
                new DirectoryMetadata(
                        this.generateId(),
                        user.getUserName(),
                        user.getUserName().toLowerCase(),
                        user.getUserName(),
                        DropboksController.getServerName(),
                        user.getId());

        DirectoryMetadata result = null;
        try {
           this.insert(usersDirectory);
        } catch (DataAccessException e){
            throw e;
        }
        return result;
    }
}
