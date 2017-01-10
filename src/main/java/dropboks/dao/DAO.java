package dropboks.dao;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.impl.UpdatableRecordImpl;

/**
 * Created by miwas on 11.01.17.
 */
public abstract class DAO<T, Record extends UpdatableRecordImpl<Record>> {

    private final String DB_URL = "jdbc:sqlite:test.db";
    private final Class<T> type;
    private final TableImpl<Record> TABLE;

    public DAO(Class<T> type) {
        this.type = type;
        this.TABLE = null;
    }

    public T store(T data) {
        try (DSLContext create = DSL.using(DB_URL)) {
            Record record = create.newRecord(TABLE, data);
            record.store();
            return record.into(type);
        }
    }
}
