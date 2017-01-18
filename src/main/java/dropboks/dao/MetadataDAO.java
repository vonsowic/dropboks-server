package dropboks.dao;

import dropboks.DropboksController;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.jooq.impl.UpdatableRecordImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miwas on 11.01.17.
 */
public abstract class MetadataDAO<T, R extends UpdatableRecordImpl<R>> extends DAO<T, R, String> {

    protected MetadataDAO(Class type, Table table, DropboksController controller) {
        super(type, table, controller);
    }

    public abstract T move(String from, String to);

    public abstract T getMetaData(String path);

    public abstract T rename(String oldName, String newName);

    public abstract TableField<R, Integer> getParentIdTableRecord();

    //public abstract T getMetadataWithChildren(Integer id, List<T> listOfChildren);

    public List<T> getListOfMetadata(List<Integer> idsList){
        List<T> listOfMetadata = new ArrayList<>();

        try (DSLContext create = DSL.using(DB_URL)) {
            for ( Integer id : idsList){
                listOfMetadata.add(
                        create.selectFrom(getTABLE())
                        .where(getIdOfTableRecord().equal(id))
                        .fetchOne()
                        .into(getType())
                );
            }
        }

       return listOfMetadata;
    }

    public List<T> getListOfChildren(Integer id){
        List<T> listOfChildren = new ArrayList<>();
        try (DSLContext create = DSL.using(DB_URL)) {
            listOfChildren.add(
                    create.selectFrom(getTABLE())
                            .where(getParentIdTableRecord().equal(id))
                            .fetchOne()
                            .into(getType())
            );

        }

        return listOfChildren;
    }

    // TODO
    public List<T> getMetadataList(Integer beggining, boolean recursive){
        List<T> list = new ArrayList<>();
        return list;
    }
}
