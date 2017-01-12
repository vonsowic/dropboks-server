package dropboks.dao;

import org.jooq.Table;
import org.jooq.impl.UpdatableRecordImpl;

/**
 * Created by miwas on 12.01.17.
 */
public abstract class ContentsDAO<T, R extends UpdatableRecordImpl<R>, SK> extends DAO<T, R, SK> {
    protected ContentsDAO(Class type, Table table) {
        super(type, table);
    }
}
