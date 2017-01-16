package dropboks.dao;

import org.jooq.DSLContext;
import org.jooq.Table;
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

    public abstract T move(String from, String to);

    public abstract T getMetaData(String path);

    public abstract T rename(String oldName, String newName);

    public List getListOfMetadata(List<Integer> idsList){
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

}
