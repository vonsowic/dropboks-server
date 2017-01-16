package dropboks.dao;

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

    private DirectoryMetadataDAO repository;

    protected ContentsDAO(Class type, Table table, DirectoryMetadataDAO repository) {
        super(type, table);
        this.repository = repository;
    }

    public abstract Integer getSecondId(T object);

    public abstract T create(Integer firstKey, Integer secondKey);

    public T move(String from, String to) throws DataAccessException{
        DirectoryMetadata newDirectory = this.findDirectoryBySecondId(
                PathResolver.getParentPath(to)
        );

        T oldDirDirCont = findByPath(from);

        T newDirDirCont = create(
                newDirectory.getFolderId(),
                getSecondId(oldDirDirCont)
        );

        delete(oldDirDirCont);
        insert(newDirDirCont);
        return newDirDirCont;
    }

    public void deleteByPath(String path) throws DataAccessException {
        T contest = findByPath(path);
        System.out.println(contest.toString());
        delete(contest);
    }

    protected T findByPath(String path) throws DataAccessException {
        System.out.println("Metadata start");
        DirectoryMetadata metadata = repository.findBySecondId(path);

        System.out.println("Metadata"+metadata.toString());
        return findBySecondId(metadata.getFolderId());
    }

    protected DirectoryMetadata findDirectoryBySecondId(String path) throws DataAccessException{
        return repository.findBySecondId(path);
    }

    public List<Integer> getListFolderContentOfId(Integer id, boolean recursive) throws DataAccessException{
        List<Integer> list;

            try (DSLContext create = DSL.using(DB_URL)) {
                list = create.select(getSecondIdOfTableRecord())
                                .from(getTABLE())
                                .where(getIdOfTableRecord().equal(id))
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
}
