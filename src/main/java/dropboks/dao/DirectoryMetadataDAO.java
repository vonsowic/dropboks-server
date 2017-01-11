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

import static pl.edu.agh.kis.florist.db.tables.FolderMetadata.FOLDER_METADATA;

/**
 * Directory Data Access Object
 * @author miwas
 */
public class DirectoryMetadataDAO extends DAO<DirectoryMetadata, FolderMetadataRecord>{
    private final String DB_URL = "jdbc:sqlite:test.db";

    private DropboksController controller;

    public DirectoryMetadataDAO(Class<DirectoryMetadata> type,
                                TableImpl<FolderMetadataRecord> table,
                                DropboksController controller) {
        super(type, table);
        this.controller = controller;
    }

    @Override
    public TableField<FolderMetadataRecord, Integer> getIdOfTableRecord() {
        return FOLDER_METADATA.FOLDER_ID;
    }

    @Override
    public TableField<FolderMetadataRecord, String> getNameIdOfTableRecord() {
        return FOLDER_METADATA.PATH_DISPLAY;
    }

    @Override
    public Integer getIdOfModel(DirectoryMetadata object) {
        return object.getFolderId();
    }

    // adds directory to existing directory
    public DirectoryMetadata store(String path) throws DataAccessException {
        DirectoryMetadata newDirectory ;
        ArrayList<String> tmpPath = new ArrayList<>(Arrays.asList(path.split("/")));

        try {
            String name = PathResolver.getUserName(path);
            User user = controller.getUsersRepository().loadOfPath(name);


            newDirectory = new DirectoryMetadata(
                    this.generateId(),
                    name,
                    path.toLowerCase(),
                    path,
                    getParentDirecoryId(tmpPath),
                    controller.getServerName(),
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
        Integer id = loadOfPath(tmp).getFolderId();
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

        DirectoryMetadata result;
        try {
           result = this.store(usersDirectory);
        } catch (DataAccessException e){
            throw e;
        }
        return result;
    }



}
