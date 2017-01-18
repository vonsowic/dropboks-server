package dropboks.dao;

import dropboks.DropboksController;
import dropboks.PathResolver;
import dropboks.model.DirectoryMetadata;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.jooq.impl.UpdatableRecordImpl;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by miwas on 12.01.17.
 */
public abstract class ContentsDAO<T, R extends UpdatableRecordImpl<R>> extends DAO<T, R, Integer> {

    protected ContentsDAO(Class type, Table table, DropboksController controller) {
        super(type, table, controller);
    }

    public abstract Integer getSecondId(T object);

    public abstract T create(Integer firstKey, Integer secondKey);

    public T move(String from, String to) throws DataAccessException{
        DirectoryMetadata newDirectory = this.findDirectoryBySecondId(
                PathResolver.getParentPath(to)
        );

        T oldDirDirCont = findByPath(from);

        T newDirDirCont = create(
                getId(oldDirDirCont),
                newDirectory.getFolderId()
        );

        delete(oldDirDirCont);
        insert(newDirDirCont);
        return newDirDirCont;
    }

    public void deleteByPath(String path) throws DataAccessException {
        T contest = findByPath(path);
        delete(contest);
    }

    protected T findByPath(String path) throws DataAccessException {
        DirectoryMetadata metadata = findDirectoryBySecondId(path);
        return findById(metadata.getFolderId());
    }

    protected DirectoryMetadata findDirectoryBySecondId(String path) throws DataAccessException{
        return getDirectoryMetadataRepo().findBySecondId(path);
    }

    public List<Integer> getListFolderContentOfId(Integer id, boolean recursive) throws DataAccessException{
        List<Integer> list;

        try (DSLContext create = DSL.using(DB_URL)) {
            list = create.select(getIdOfTableRecord())
                    .from(getTABLE())
                    .where(getSecondIdOfTableRecord().equal(id))
                    .fetchInto(Integer.class);
        }

        if (recursive){
            List<Integer> tmpList = new ArrayList<>(list);
            for (Integer childId : tmpList){
                list.addAll(getListFolderContentOfId(childId, recursive));
            }
        }

        return list;
    }

    protected DirectoryMetadataDAO getDirectoryMetadataRepo(){
        return getController().getDirectoryMetadataRepository();
    }
}
