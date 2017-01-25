package dropboks.dao;

import dropboks.model.User;
import org.jooq.TableField;
import org.jooq.impl.TableImpl;
import pl.edu.agh.kis.florist.db.tables.records.UsersRecord;


import static pl.edu.agh.kis.florist.db.Tables.USERS;

/**
 * Created by miwas on 08.01.17.
 */
public class UsersDAO extends DAO<User, UsersRecord, String> {

    public UsersDAO(Class type, TableImpl table) {
        super(type, table);
    }

    @Override
    public TableField getIdOfTableRecord() {
        return USERS.ID;
    }

    @Override
    public TableField getSecondIdOfTableRecord() {
        return USERS.USER_NAME;
    }

    @Override
    public Integer getId(User object) {
        return object.getId();
    }

}
