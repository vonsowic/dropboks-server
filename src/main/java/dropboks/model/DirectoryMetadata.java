package dropboks.model;

import pl.edu.agh.kis.florist.db.tables.pojos.FolderMetadata;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by miwas on 08.01.17.
 */
public class DirectoryMetadata extends FolderMetadata implements Recursion<DirectoryMetadata> {

    List<Object> listOfMetadata;

    public DirectoryMetadata(FolderMetadata value) {
        super(value);
    }

    public DirectoryMetadata(Integer folderId, String name, String pathLower, String pathDisplay, Integer parentFolderId, Timestamp serverCreatedAt) {
        super(folderId, name, pathLower, pathDisplay, parentFolderId, serverCreatedAt);
    }

    public DirectoryMetadata(String name, String pathLower, String pathDisplay, Integer parentFolderId, Timestamp serverCreatedAt) {
        super(null, name, pathLower, pathDisplay, parentFolderId, serverCreatedAt);
    }

    public DirectoryMetadata(DirectoryMetadata value, List<Object> list){
        super(value);
        this.listOfMetadata = list;
    }


    @Override
    public DirectoryMetadata appendChildren(List list) {
        return new DirectoryMetadata(this, list);

    }
}
