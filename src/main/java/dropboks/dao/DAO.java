package dropboks.dao;

import org.jooq.DSLContext;
import org.jooq.TableField;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.impl.UpdatableRecordImpl;

import java.util.List;

/**
 * Main class for data access objects. Gives CRUD and other cool elementary methods for database.
 * @author miwas
 * @version 1.0
 */
public abstract class DAO<T, Record extends UpdatableRecordImpl<Record>> {

    /**
     *
     */
    String DB_URL = "jdbc:sqlite:test.db";
    private final Class<T> type;
    private final TableImpl<Record> TABLE;

    public DAO(Class<T> type, TableImpl<Record> table) {
        this.type = type;
        this.TABLE = table;
    }


    /**
     * @return ID TableField from Database.
     */
    public abstract TableField<Record, Integer> getIdOfTableRecord();

    /**
     * @return Name (like DISPALAY_NAME or USER_NAME ) from Database.
     */
    public abstract TableField<Record, String> getNameIdOfTableRecord();

    /**
     * Get id of model from database.
     * @param object of class from package model
     * @return object id.
     */
    public abstract Integer getIdOfModel(T object);


    /**
     * Get id.
     * @param path to object in Database
     * @return object id
     * @see #getNameIdOfTableRecord
     */
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


    /**
     * Store object to database.
     * @param data object of class from package model
     * @return result
     */
    public T store(T data) {
        try (DSLContext create = DSL.using(DB_URL)) {
            Record record = create.newRecord(TABLE, data);
            record.store();
            return record.into(type);
        }
    }

    /**
     * Generate id for object to database
     * @return generated id.

    // TODO: jesli zostanie usuniety element ze srodka bazy, to otrzmane pozniej id bedzie bledne
    public Integer generateId(){
        try (DSLContext create = DSL.using(DB_URL)) {
            List<T> list =
                    create.select(TABLE.fields())
                            .from(TABLE)
                            .fetchInto(type);
            return list.size();
        }
    }
     */

    /**
     * Check if object based on name exists in database.
     * @param name of object in Database (like DISPLAY_PATH or USER_NAME.
     * @return true if object exist in database
     */
    public boolean exists(String name){
        try (DSLContext create = DSL.using(DB_URL)) {
            return create.fetchExists(create.selectFrom(TABLE).where(getNameIdOfTableRecord().equal(name)));
        }
    }

    /**
     *
     * @param name of object in Database (like DISPLAY_PATH or USER_NAME).
     * @return object of class #T from database.
     * @throws DataAccessException jooq exception
     */
    public T loadOfPath(String name) throws DataAccessException{
        T object;
        try (DSLContext create = DSL.using(DB_URL)) {
            Record record = create.selectFrom(TABLE).where(getNameIdOfTableRecord().equal(name)).fetchOne();
            object = record.into(this.type);

        } catch (DataAccessException e){
            throw e;
        }
        return object;
    }

    /**
     *
     * @param id of object in Database (like ID or FILE_ID).
     * @return object of class #T from database.
     * @throws DataAccessException jooq exception
     */
    public T loadOfId(Integer id) throws DataAccessException{
        T user;
        try (DSLContext create = DSL.using(DB_URL)) {
            Record record = create.selectFrom(TABLE).where(getIdOfTableRecord().equal(id)).fetchOne();
            user = record.into(this.type);
        } catch (DataAccessException e){
            throw e;
        }
        return user;
    }

    /**
     * Removes object based on path from database.
     * @param path to object in Database
     */
    public void remove(String path) {
        try (DSLContext create = DSL.using(DB_URL)) {
            create.delete(TABLE).where(getNameIdOfTableRecord().equal(path)).execute();
        }
    }

    /**
     * Removes object based on id from database.
     * @param id to object in Database
     */
    public void remove(Integer id) {
        try (DSLContext create = DSL.using(DB_URL)) {
            create.delete(TABLE).where(getIdOfTableRecord().equal(id)).execute();
        }
    }
}
