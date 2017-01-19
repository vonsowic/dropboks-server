package dropboks.dao;

import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.jooq.impl.UpdatableRecordImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miwas on 11.01.17.
 */
public abstract class MetadataDAO<T, R extends UpdatableRecordImpl<R>> extends DAO<T, R, String> {

    protected MetadataDAO(Class type, Table table) {
        super(type, table);
    }

    public abstract T move(String from, String to, Integer parentId);

    public abstract T rename(String oldName, String newName);

    public abstract TableField<R, Integer> getParentIdTableRecord();

    public abstract T getMetadataWithChildren(Integer id, List<Object> listOfChildren);

    public List<T> getListOfChildren(Integer id){
        try (DSLContext create = DSL.using(DB_URL)) {
            List<T> list =
                    create.select(getTABLE().fields())
                            .from(getTABLE())
                            .where(getParentIdTableRecord().equal(id))
                            .fetchInto(getType());
            return list;
        }
    }

    public List getMetadataList(Integer beggining, boolean recursive){
        if ( !recursive ){
            return getListOfChildren(beggining);
        }

        List<T> list = new ArrayList<>();
        List<T> childrenList = getListOfChildren(beggining);

        for (T child : childrenList){
            Integer childId = getId(child);
            list.add((T) getMetadataWithChildren(childId, getMetadataList(childId, true)));
        }

        return list;
    }

    public T getMetaData(String path) {
        return findBySecondId(path);
    }
}
