package dropboks.dao;

import dropboks.App;
import dropboks.controllers.DropboksController;
import dropboks.model.User;
import org.jooq.DSLContext;
import org.jooq.TableField;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import pl.edu.agh.kis.florist.db.tables.records.UsersRecord;

import java.util.List;

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

    public List<User> loadAllUsers() throws DataAccessException {
        try (DSLContext create = DSL.using(DB_URL)) {
            List<User> users =
                    create.select(USERS.fields())
                            .from(USERS)
                            .fetchInto(User.class);

            return users;
        }
    }
}
