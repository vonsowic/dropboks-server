package dropboks.dao;

import org.jooq.DSLContext;
import org.jooq.TableField;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.impl.UpdatableRecordImpl;

import java.util.List;

/**
 * Created by miwas on 11.01.17.
 */
public abstract class DAO<T, Record extends UpdatableRecordImpl<Record>> {

    private final String DB_URL = "jdbc:sqlite:test.db";
    private final Class<T> type;
    private final TableImpl<Record> TABLE;
    private TableField<Record, Integer> ID;

    public DAO(Class<T> type, TableImpl<Record> table) {
        this.type = type;
        this.TABLE = table;
    }

    public abstract TableField<Record, Integer> getIdOfTableRecord();

    public abstract TableField<Record, String> getNameIdOfTableRecord();

    public abstract Integer getIdOfModel(T object);


    public Integer getId(String path){
        Integer id = null;
        try {
            T data = loadOfPath(path);
            id = getIdOfModel(data);
        } catch (DataAccessException e){
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        return id;
    }


    public T store(T data) {
        try (DSLContext create = DSL.using(DB_URL)) {
            Record record = create.newRecord(TABLE, data);
            record.store();
            return record.into(type);
        }
    }

    public Integer generateId(){
        try (DSLContext create = DSL.using(DB_URL)) {
            List<T> list =
                    create.select(TABLE.fields())
                            .from(TABLE)
                            .fetchInto(type);
            return list.size();
        }
    }

    public boolean exists(String name){
        try (DSLContext create = DSL.using(DB_URL)) {
            return create.fetchExists(create.selectFrom(TABLE).where(getNameIdOfTableRecord().equal(name)));
        }
    }

    public T loadOfPath(String name) throws DataAccessException{
        T user = null;
        try (DSLContext create = DSL.using(DB_URL)) {
            Record record = create.selectFrom(TABLE).where(getNameIdOfTableRecord().equal(name)).fetchOne();
            user = record.into(this.type);

        } catch (DataAccessException e){
            e.printStackTrace();
        }
        return user;
    }

    public T loadOfId(Integer id) throws DataAccessException{
        T user = null;
        try (DSLContext create = DSL.using(DB_URL)) {
            Record record = create.selectFrom(TABLE).where(getIdOfTableRecord().equal(id)).fetchOne();
            user = record.into(this.type);

        } catch (DataAccessException e){
            e.printStackTrace();
        }
        return user;
    }

    public void remove(String path) {
        try (DSLContext create = DSL.using(DB_URL)) {
            //create.fetchExists(create.selectFrom(FOLDER_METADATA).where(FOLDER_METADATA.PATH_DISPLAY.equal(path)));
            create.delete(TABLE).where(getNameIdOfTableRecord().equal(path)).execute();
        }
    }
}
