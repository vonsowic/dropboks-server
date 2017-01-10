package dropboks.dao;

import com.google.common.base.Joiner;
import dropboks.DropboksController;
import dropboks.model.DirectoryMetadata;
import dropboks.model.User;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import pl.edu.agh.kis.florist.db.tables.records.FolderMetadataRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static pl.edu.agh.kis.florist.db.tables.FolderMetadata.FOLDER_METADATA;

/**
 * Directory Data Access Object
 * @author miwas
 */
public class DirectoryMetadataDAO {
    private final String DB_URL = "jdbc:sqlite:test.db";

    private DropboksController controller;
    public DirectoryMetadataDAO(DropboksController controller) {
        this.controller = controller;
    }

    public DirectoryMetadata findUsersDirectoryByHisId(int id){
        try (DSLContext create = DSL.using(DB_URL)) {
            FolderMetadataRecord record = create.selectFrom(FOLDER_METADATA).where(FOLDER_METADATA.OWNER_ID.equal(id)).fetchOne();
            DirectoryMetadata directoryMetadata = record.into(DirectoryMetadata.class);
            return directoryMetadata;
        }
    }

    public DirectoryMetadata loadDirOfId(int dirId) {
        try (DSLContext create = DSL.using(DB_URL)) {
            FolderMetadataRecord record = create.selectFrom(FOLDER_METADATA).where(FOLDER_METADATA.FOLDER_ID.equal(dirId)).fetchOne();
            DirectoryMetadata directory = record.into(DirectoryMetadata.class);
            return directory;
        }
    }

    public DirectoryMetadata loadDirOfPath(String path) throws DataAccessException {
        try (DSLContext create = DSL.using(DB_URL)) {
            FolderMetadataRecord record = create.selectFrom(FOLDER_METADATA).where(FOLDER_METADATA.PATH_DISPLAY.equal(path)).fetchOne();
            DirectoryMetadata directory = record.into(DirectoryMetadata.class);
            return directory;
        } catch (DataAccessException ex){
            throw ex;
        }
    }

    // adds directory to existing directory
    public DirectoryMetadata store(String path) throws DataAccessException {
        DirectoryMetadata newDirectory ;
        ArrayList<String> tmpPath = new ArrayList<>(Arrays.asList(path.split("/")));

        try {
            User user = controller.getUsersRepository().loadUserByName(tmpPath.get(0));
            String name = tmpPath.get(tmpPath.size()-1);

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

    public DirectoryMetadata store(DirectoryMetadata directoryMetadata){
        try (DSLContext create = DSL.using(DB_URL)) {
            FolderMetadataRecord record = create.newRecord(FOLDER_METADATA, directoryMetadata);
            record.store();
            return record.into(DirectoryMetadata.class);
        } catch (DataAccessException e) {
            throw e;
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

    public Integer getParentDirecoryId(ArrayList<String> path){
        String tmp;
        if (path.size()>1){
            tmp = Joiner.on("/").join(path.subList(0, path.size()-1));
        } else {
            tmp = path.toString();
        }
        Integer id = loadDirOfPath(tmp).getFolderId();
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

    public boolean exists(String path) {
        try (DSLContext create = DSL.using(DB_URL)) {
            return create.fetchExists(create.selectFrom(FOLDER_METADATA).where(FOLDER_METADATA.PATH_DISPLAY.equal(path)));
        }
    }

    public void remove(String path) {
        try (DSLContext create = DSL.using(DB_URL)) {
            //create.fetchExists(create.selectFrom(FOLDER_METADATA).where(FOLDER_METADATA.PATH_DISPLAY.equal(path)));
            create.delete(FOLDER_METADATA).where(FOLDER_METADATA.PATH_DISPLAY.equal(path)).execute();
        }
    }

    public Integer getId(String path){
        Integer id = null;
        try {
            DirectoryMetadata dir = loadDirOfPath(path);
            id = dir.getFolderId();
        } catch (DataAccessException e){
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        return id;
    }
}
