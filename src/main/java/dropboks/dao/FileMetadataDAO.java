package dropboks.dao;

import dropboks.PathResolver;
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

    public FileMetadataDAO(Class<FileMetadata> type, TableImpl<FileMetadataRecord> table) {
        super(type, table);
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

    @Override
    public FileMetadata getMetadataWithChildren(Integer id, List<Object> listOfChildren) {
        return new FileMetadata(findById(id), listOfChildren);
    }

    @Override
    public FileMetadata move(String path, String newPath, Integer parentId) {
        FileMetadata record = this.findBySecondId(path);

        record.setName(PathResolver.getName(newPath));
        record.setPathDisplay(newPath);
        record.setPathLower(newPath.toLowerCase());
        record.setEnclosingFolderId(parentId);

        update(record);
        return record;
    }

    @Override
    public FileMetadata rename(String oldPath, String newPath) {
        FileMetadata fileMetadata = findBySecondId(oldPath);
        fileMetadata.setName(PathResolver.getName(newPath));
        fileMetadata.setPathDisplay(newPath);
        fileMetadata.setPathLower(newPath.toLowerCase());
        update(fileMetadata);
        return fileMetadata;
    }

    @Override
    protected Integer getId(FileMetadata object) {
        return object.getFileId();
    }

    public FileMetadata create(String path, Integer parentFolderId, Integer size){
        return new FileMetadata(
                PathResolver.getName(path),
                path.toLowerCase(),
                path,
                size,
                time(),
                parentFolderId
                );
    }

    public List getMetadataList(Integer id){
        return getMetadataList(id, false);
    }
}
