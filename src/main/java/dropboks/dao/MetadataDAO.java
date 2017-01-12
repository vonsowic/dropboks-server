package dropboks.dao;

import org.jooq.Table;
import org.jooq.impl.UpdatableRecordImpl;

/**
 * Created by miwas on 11.01.17.
 */
public abstract class MetadataDAO<T, R extends UpdatableRecordImpl<R>, SK> extends DAO<T, R, SK> {
    protected MetadataDAO(Class type, Table table) {
        super(type, table);
    }

    public abstract T move(SK from, SK to);

    public abstract T getMetaData(SK path);

    public abstract T rename(SK oldName, SK newName);

}
