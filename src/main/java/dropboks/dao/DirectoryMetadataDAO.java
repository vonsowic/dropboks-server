package dropboks.dao;

import dropboks.DropboksController;
import dropboks.PathResolver;
import dropboks.model.DirectoryMetadata;
import dropboks.model.FileMetadata;
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
    public DirectoryMetadata getMetadataWithChildren(Integer id, List<DirectoryMetadata> listOfChildren) {
        return new DirectoryMetadata(findById(id), listOfChildren);
    }

    @Override
    protected Integer getId(DirectoryMetadata object) {
        return object.getFolderId();
    }

    @Override
    public DirectoryMetadata move(String path, String newPath) {
        DirectoryMetadata record = this.findBySecondId(path);
        DirectoryMetadata newDirectory = this.findBySecondId(PathResolver.getParentPath(newPath));

        rename(path, newPath);
        record.setParentFolderId(newDirectory.getFolderId());

        this.update(record);
        return record;
    }

    @Override
    public DirectoryMetadata rename(String oldPath, String newPath) {
        // find directory by path
        DirectoryMetadata directoryMetadata = findBySecondId(oldPath);

        // change this direcotry
        directoryMetadata.setName(PathResolver.getName(newPath));
        directoryMetadata.setPathLower(newPath.toLowerCase());
        directoryMetadata.setPathDisplay(newPath);

        // get lists of children below
        List<DirectoryMetadata> childrenList = getListOfChildren(directoryMetadata.getFolderId());
        List<FileMetadata> filesList = getController().getFilesMetadataRepository().getListOfChildren(directoryMetadata.getFolderId());

        // rename children
        for (DirectoryMetadata child : childrenList){
            rename(oldPath+"/"+child.getName(), newPath+"/"+child.getName());
        }

        for (FileMetadata child : filesList){
            getController()
                    .getFilesMetadataRepository()
                    .rename(
                            oldPath+"/"+child.getName(),
                            newPath+"/"+child.getName()
                    );
        }

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

    public DirectoryMetadata createDirectoryForUser(String user) {
        DirectoryMetadata usersDirectory =
                new DirectoryMetadata(
                        user,
                        user.toLowerCase(),
                        user,
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

    @Override
    public void delete(String path){
        DirectoryMetadata object = findBySecondId(path);
        delete(getId(object));
    }


    public void delete(Integer id){
        List<DirectoryMetadata> childrenList = getListOfChildren(id);
        List<FileMetadata> fileList = getController()
                .getFilesMetadataRepository()
                .getListOfChildren(id);

        // order your children to kill theirs children and themselves
        for ( DirectoryMetadata child : childrenList){
            delete(getId(child));
        }

        // destroy your files
        for ( FileMetadata child : fileList){
            getController().getFilesMetadataRepository().delete(child);
            getController().getFilesContentRepository().deleteById(child.getFileId());
        }

        // kill yourself
        deleteById(id);
    }
}
