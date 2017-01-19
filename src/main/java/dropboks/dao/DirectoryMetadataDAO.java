package dropboks.dao;

import dropboks.PathResolver;
import dropboks.model.DirectoryMetadata;
import org.jooq.TableField;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.TableImpl;
import pl.edu.agh.kis.florist.db.tables.records.FolderMetadataRecord;

import java.util.List;

import static pl.edu.agh.kis.florist.db.tables.FolderMetadata.FOLDER_METADATA;

/**
 * Directory Data Access Object
 * @author miwas
 */
public class DirectoryMetadataDAO extends MetadataDAO<DirectoryMetadata, FolderMetadataRecord> {

    public DirectoryMetadataDAO(Class<DirectoryMetadata> type,
                                TableImpl<FolderMetadataRecord> table) {
        super(type, table);
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
    public DirectoryMetadata getMetadataWithChildren(Integer id, List<Object> listOfChildren) {
        return new DirectoryMetadata(findById(id), listOfChildren);
    }

    @Override
    protected Integer getId(DirectoryMetadata object) {
        return object.getFolderId();
    }

    @Override
    public DirectoryMetadata move(String path, String newPath, Integer parentId) {
        DirectoryMetadata record = this.findBySecondId(path);
        record.setParentFolderId(parentId);

        this.update(record);
        return record;
    }

    public DirectoryMetadata move(String path, String newPath) {
        DirectoryMetadata parent = this.findBySecondId(PathResolver.getParentPath(newPath));
        return move(path, newPath, parent.getFolderId());
    }

    @Override
    public DirectoryMetadata rename(String oldPath, String newPath) {
        DirectoryMetadata directoryMetadata = findBySecondId(oldPath);

        // change this direcotry
        directoryMetadata.setName(PathResolver.getName(newPath));
        directoryMetadata.setPathLower(newPath.toLowerCase());
        directoryMetadata.setPathDisplay(newPath);

        update(directoryMetadata);
        return directoryMetadata;
    }

    public DirectoryMetadata store(String path) throws DataAccessException {
        DirectoryMetadata newDirectory ;
        Integer id = null;
        try {
            id = findBySecondId(PathResolver.getParentPath(path)).getFolderId();

        } catch (NullPointerException e){
            e.printStackTrace();
        }

        try {
            newDirectory = new DirectoryMetadata(
                    PathResolver.getName(path),
                    path.toLowerCase(),
                    path,
                    id,
                    time()
            );
        } catch (DataAccessException e){
            throw e;
        }
        return store( newDirectory );
    }
}
