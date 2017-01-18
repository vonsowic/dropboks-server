package dropboks.dao;

import dropboks.DropboksController;
import org.jooq.*;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.*;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Main class for data access objects. Gives CRUD and other cool elementary methods for database.
 * SK - Second Key.
 * @author miwas
 * @version 1.0
 * @see @https://www.jooq.org/doc/3.9/manual/sql-execution/daos/
 * @see @https://www.jooq.org/javadoc/3.5.0/org/jooq/impl/DAOImpl.html
 */
public abstract class DAO<T, Record extends UpdatableRecordImpl<Record>, SK>
        extends DAOImpl<Record, T, Integer> {

    /**
     * Database Url for default configuration.
     */
    String DB_URL = "jdbc:sqlite:test.db";


    /**
     * Type of class representing records from database.
     */
    private final Class<T> type;


    /**
     * Reference to table in Database.
     */
    private final TableImpl<Record> TABLE;

    private DropboksController controller;



    protected DAO( Class<T> type, Table<Record> table, DropboksController controller) {
        super(table, type);
        this.type = type;
        TABLE = (TableImpl<Record>) table;
        this.controller = controller;

        try {
            this.setConfiguration(new DefaultConfiguration().set(DriverManager.getConnection(DB_URL)).set(SQLDialect.SQLITE));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * @return ID TableField from Database.
     */
    public abstract TableField<Record, Integer> getIdOfTableRecord();

    /**
     * @return Name (like DISPLAY_NAME or USER_NAME ) from Database.
     */
    public abstract TableField<Record, SK> getSecondIdOfTableRecord();


    /**
     * Get id.
     * @param key to object in Database
     * @return object id
     * @see #getSecondIdOfTableRecord()
     */
    public Integer getIdBySecondId(SK key) throws DataAccessException{
        T data = findBySecondId(key);
        return getId(data);
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
    */
    public Integer generateId(){
        /*
        ArrayList<T> list = (ArrayList<T>) loadAll();
        Integer id = 0;
        for(T object : list){
            if ( this.getId(object) > id) {
                id = this.getId(object);
            }
        }
        return id+1;
        */
       return null;
    }


    /**
     * Checks using second id if object exists in database.
     * @param key to object in Database (like DISPLAY_PATH or USER_NAME.
     * @return true if object exist in database
     */
    public boolean existsBySecondId(SK key){
        try (DSLContext create = DSL.using(DB_URL)) {
            return create.fetchExists(create.selectFrom(TABLE).where(getSecondIdOfTableRecord().equal(key)));
        }
    }

    /**
     * Loads record from database based on its second key ( like USER_NAME or PATH_DISPLAY)
     * @param key to object in Database (like DISPLAY_PATH or USER_NAME).
     * @return object of class #T from database.
     * @throws @DataAccessException jooq exception
     * @see @DataAccessException
     */
    public T findBySecondId(SK key) throws DataAccessException{
        try (DSLContext create = DSL.using(DB_URL)) {
            Record record = create.selectFrom(TABLE)
                    .where(getSecondIdOfTableRecord()
                            .equal(key))
                    .fetchOne();

            T object = record.into(this.type);
            return object;
        }
    }

    /**
     * Loads all records to list from table.
     * @return list containing all records.
     */
    public List<T> loadAll(){
        try (DSLContext create = DSL.using(DB_URL)) {
            List<T> list =
                    create.select(TABLE.fields())
                            .from(TABLE)
                            .fetchInto(type);

            return list;
        }
    }


    /**
     * Removes object based on second key from database.
     * @param key is second key to object in Database
     */
    public void deleteBySecondId(SK key) {
        try (DSLContext create = DSL.using(DB_URL)) {
            create.delete(TABLE).where(getSecondIdOfTableRecord().equal(key)).execute();
        }
    }

    /**
     * @return type of class stored in the table
     */
    @Override
    public Class<T> getType() {
        return type;
    }

    /**
     * @return reference to table from database.
     */
    public TableImpl<Record> getTABLE() {
        return TABLE;
    }

    public DropboksController getController() {
        return controller;
    }
}
